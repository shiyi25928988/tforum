package cc.shiyi.coleditor.forum.controller.oss;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.oss.common.UploadService;
import cc.shiyi.oss.items.FileItem;
import cc.shiyi.oss.services.MinioFileDeleteService;
import cc.shiyi.oss.services.MinioFileDownloadService;
import cc.shiyi.oss.services.MinioFileListService;
import cc.shiyi.oss.utils.DownloadUtil;
import cc.shiyi.oss.utils.MinioUrlUtil;
import io.minio.GetObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "OSS 文件管理-OssController")
public class OssController {

    private UploadService uploadService;
    private MinioFileListService minioFileListService;
    private MinioFileDeleteService minioFileDeleteService;
    private MinioFileDownloadService minioFileDownloadService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/api/v1/oss/upload", consumes = "multipart/form-data")
    public ResponseWrapper<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = uploadService.uploadFile(file);
        return new ResponseWrapper<String>().success(MinioUrlUtil.toProxyUrl(url), "上传成功");
    }

    @Operation(summary = "上传文件到指定目录")
    @PostMapping(value = "/api/v1/oss/uploadToFolder", consumes = "multipart/form-data")
    public ResponseWrapper<String> uploadToFolder(
            @RequestParam("file") MultipartFile file,
            @RequestParam String folder) throws Exception {
        String url = uploadService.setFolder(folder).uploadFile(file);
        return new ResponseWrapper<String>().success(MinioUrlUtil.toProxyUrl(url), "上传成功");
    }

    @Operation(summary = "获取文件列表")
    @GetMapping("/api/v1/oss/list")
    public ResponseWrapper<List<FileItem>> list() throws Exception {
        return new ResponseWrapper<List<FileItem>>().success(minioFileListService.listFile());
    }

    @Operation(summary = "获取指定目录的文件列表")
    @GetMapping("/api/v1/oss/listByFolder")
    public ResponseWrapper<List<FileItem>> listByFolder(@RequestParam String folder) throws Exception {
        return new ResponseWrapper<List<FileItem>>().success(minioFileListService.listFile(folder));
    }

    @Operation(summary = "删除文件")
    @PostMapping("/api/v1/oss/delete")
    public ResponseWrapper<?> delete(@RequestParam String fileName) throws Exception {
        minioFileDeleteService.deleteFile(fileName);
        return new ResponseWrapper<>().success("删除成功");
    }

    @Operation(summary = "下载文件")
    @GetMapping("/api/v1/oss/download")
    public ResponseWrapper<String> download(@RequestParam String url) throws Exception {
        File file = DownloadUtil.download(url);
        return new ResponseWrapper<String>().success(file.getAbsolutePath(), "下载成功");
    }

    @Operation(summary = "代理转发 MinIO 文件（流式传输）")
    @GetMapping("/api/v1/oss/file")
    public void proxyFile(@RequestParam String url,
                          @RequestParam(defaultValue = "false") boolean download,
                          HttpServletResponse response) throws Exception {
        // 1. 解析并验证 URL
        String[] bucketAndObject;
        try {
            bucketAndObject = MinioUrlUtil.resolveBucketAndObject(url);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            return;
        }
        String bucket = bucketAndObject[0];
        String objectName = bucketAndObject[1];

        // 2. 从 MinIO 获取对象
        GetObjectResponse objectResponse;
        try {
            objectResponse = minioFileDownloadService.getObject(bucket, objectName);
        } catch (io.minio.errors.ErrorResponseException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"文件不存在: " + objectName + "\"}");
            return;
        }

        // 3. 设置响应头
        // Content-Type
        String contentType = objectResponse.headers().get("Content-Type");
        if (contentType == null || contentType.isEmpty()) {
            contentType = cc.shiyi.oss.http.ContentType.getTypeByFileName(objectName);
        }
        response.setContentType(contentType);

        // Content-Length
        String contentLength = objectResponse.headers().get("Content-Length");
        if (contentLength != null && !contentLength.isEmpty()) {
            response.setContentLengthLong(Long.parseLong(contentLength));
        }

        // Content-Disposition
        if (download) {
            String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + encodedFileName);
        } else {
            response.setHeader("Content-Disposition", "inline");
        }

        // 4. 流式传输
        byte[] buffer = new byte[8192];
        int bytesRead;
        try (InputStream in = objectResponse; OutputStream out = response.getOutputStream()) {
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            // 客户端断开连接或发生其他 IO 错误
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

}
