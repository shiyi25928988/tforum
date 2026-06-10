package cc.shiyi.coleditor.forum.controller.book;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.BookService;
import cc.shiyi.coleditor.forum.table.Book;
import cc.shiyi.coleditor.user.service.UserService;
import cc.shiyi.oss.common.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "图书角管理-BookController")
public class BookController {

    private BookService bookService;
    private UserService userService;
    private UploadService uploadService;

    @Operation(summary = "根据id获取图书")
    @GetMapping("/api/v1/book/{id}")
    public ResponseWrapper<Book> getById(@PathVariable("id") Long id) {
        return new ResponseWrapper<Book>().success(bookService.getById(id));
    }

    @Operation(summary = "分页获取图书列表")
    @GetMapping("/api/v1/book/list")
    public ResponseWrapper<Page<Book>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return new ResponseWrapper<Page<Book>>().success(
                bookService.listByPage(pageNum, pageSize, categoryId, keyword));
    }

    @Operation(summary = "上传 PDF 图书")
    @PostMapping(value = "/api/v1/book/upload", consumes = "multipart/form-data")
    public ResponseWrapper<Book> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long categoryId) throws Exception {

        // 计算文件 SHA-256 哈希
        String fileHash = sha256(file.getBytes());

        // 通过哈希值检查是否已存在相同文件
        if (bookService.existsByHash(fileHash)) {
            return new ResponseWrapper<Book>().fail("该 PDF 文件已存在，请勿重复上传");
        }

        // 上传 PDF 到 OSS
        String fileUrl = uploadService
                .setFolder("books/")
                .uploadFile(file);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setCategoryId(categoryId);
        book.setFileUrl(fileUrl);
        book.setFileSize(file.getSize());
        book.setFileHash(fileHash);
        if (userService.getCurrentUser() != null) {
            book.setUploaderId(userService.getCurrentUser().getId());
        }

        return new ResponseWrapper<Book>().success(bookService.save(book));
    }

    @Operation(summary = "删除图书")
    @PostMapping("/api/v1/book/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        bookService.delete(id);
        return new ResponseWrapper<>().success();
    }

    /** 计算字节数组的 SHA-256 哈希值 */
    private String sha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return HexFormat.of().formatHex(hash);
    }

    @Operation(summary = "记录下载")
    @PostMapping("/api/v1/book/download")
    public ResponseWrapper<?> download(@RequestParam Long id) {
        bookService.increaseDownload(id);
        Book book = bookService.getById(id);
        return new ResponseWrapper<String>().success(book.getFileUrl(), "");
    }
}
