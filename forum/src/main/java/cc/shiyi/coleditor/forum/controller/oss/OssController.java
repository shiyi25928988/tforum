package cc.shiyi.coleditor.forum.controller.oss;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.oss.common.UploadService;
import cc.shiyi.oss.items.FileItem;
import cc.shiyi.oss.services.MinioFileDeleteService;
import cc.shiyi.oss.services.MinioFileListService;
import cc.shiyi.oss.utils.DownloadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "OSS 文件管理-OssController")
public class OssController {

    private UploadService uploadService;
    private MinioFileListService minioFileListService;
    private MinioFileDeleteService minioFileDeleteService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/api/v1/oss/upload", consumes = "multipart/form-data")
    public ResponseWrapper<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = uploadService.uploadFile(file);
        return new ResponseWrapper<String>().success(url, "上传成功");
    }

    @Operation(summary = "上传文件到指定目录")
    @PostMapping(value = "/api/v1/oss/uploadToFolder", consumes = "multipart/form-data")
    public ResponseWrapper<String> uploadToFolder(
            @RequestParam("file") MultipartFile file,
            @RequestParam String folder) throws Exception {
        String url = uploadService.setFolder(folder).uploadFile(file);
        return new ResponseWrapper<String>().success(url, "上传成功");
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

}
