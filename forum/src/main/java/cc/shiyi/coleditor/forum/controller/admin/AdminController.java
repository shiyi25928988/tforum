package cc.shiyi.coleditor.forum.controller.admin;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.AsyncDocumentService;
import cc.shiyi.coleditor.forum.mapper.ArticleMapper;
import cc.shiyi.coleditor.forum.mapper.ArticleTagMapper;
import cc.shiyi.coleditor.forum.mapper.BookMapper;
import cc.shiyi.coleditor.forum.mapper.ForumPostMapper;
import cc.shiyi.coleditor.forum.table.Article;
import cc.shiyi.coleditor.forum.table.ArticleTag;
import cc.shiyi.coleditor.forum.table.Book;
import cc.shiyi.coleditor.forum.mapper.SkillMapper;
import cc.shiyi.coleditor.forum.table.Skill;
import cc.shiyi.coleditor.forum.table.ForumPost;
import cc.shiyi.coleditor.user.mapper.UserMapper;
import cc.shiyi.coleditor.user.table.User;
import cc.shiyi.oss.services.MinioFileDeleteService;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "管理后台-AdminController")
public class AdminController {

    private UserMapper userMapper;
    private ArticleMapper articleMapper;
    private ForumPostMapper forumPostMapper;
    private BookMapper bookMapper;
    private ArticleTagMapper articleTagMapper;
    private AsyncDocumentService asyncDocumentService;
    private MinioFileDeleteService minioFileDeleteService;
    private SkillMapper skillMapper;

    /** 校验当前用户是否管理员 */
    private boolean isAdmin() {
        Long id = StpUtil.getLoginIdAsLong();
        if (id == null) return false;
        User user = userMapper.selectById(id);
        return user != null && "admin".equals(user.getRole());
    }

    private ResponseWrapper<?> checkAdmin() {
        if (!isAdmin()) {
            return new ResponseWrapper<>().fail("无管理员权限");
        }
        return null;
    }

    @Operation(summary = "管理后台仪表盘")
    @GetMapping("/api/v1/admin/dashboard")
    public ResponseWrapper<Map<String, Object>> dashboard() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<Map<String, Object>>) check;

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(new QueryWrapper<>()));
        stats.put("articleCount", articleMapper.selectCount(new QueryWrapper<>()));
        stats.put("postCount", forumPostMapper.selectCount(new QueryWrapper<>()));
        stats.put("bookCount", bookMapper.selectCount(new QueryWrapper<>()));
        return new ResponseWrapper<Map<String, Object>>().success(stats);
    }

    // ============================
    // 用户管理
    // ============================

    @Operation(summary = "用户列表")
    @GetMapping("/api/v1/admin/users")
    public ResponseWrapper<List<User>> listUsers() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<User>>) check;
        return new ResponseWrapper<List<User>>().success(userMapper.selectList(new QueryWrapper<>()));
    }

    @Operation(summary = "禁用/启用用户")
    @PostMapping("/api/v1/admin/user/toggleStatus")
    public ResponseWrapper<?> toggleUserStatus(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setStatus("disabled".equals(user.getStatus()) ? "active" : "disabled");
            userMapper.updateById(user);
        }
        return new ResponseWrapper<>().success();
    }

    // ============================
    // 文章管理
    // ============================

    @Operation(summary = "文章管理列表")
    @GetMapping("/api/v1/admin/articles")
    public ResponseWrapper<List<Article>> listArticles() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<Article>>) check;
        return new ResponseWrapper<List<Article>>().success(articleMapper.selectList(new QueryWrapper<>()));
    }

    @Operation(summary = "管理员删除文章")
    @PostMapping("/api/v1/admin/article/delete")
    public ResponseWrapper<?> deleteArticle(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        articleMapper.deleteById(id);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "管理员置顶/取消置顶文章")
    @PostMapping("/api/v1/admin/article/togglePin")
    public ResponseWrapper<?> togglePin(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setIsPinned(article.getIsPinned() == 1 ? 0 : 1);
            articleMapper.updateById(article);
        }
        return new ResponseWrapper<>().success();
    }

    // ============================
    // 讨论管理
    // ============================

    @Operation(summary = "讨论管理列表")
    @GetMapping("/api/v1/admin/posts")
    public ResponseWrapper<List<ForumPost>> listPosts() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<ForumPost>>) check;
        return new ResponseWrapper<List<ForumPost>>().success(forumPostMapper.selectList(new QueryWrapper<>()));
    }

    @Operation(summary = "管理员删除帖子")
    @PostMapping("/api/v1/admin/post/delete")
    public ResponseWrapper<?> deletePost(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        forumPostMapper.deleteById(id);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "管理员更新帖子")
    @PostMapping("/api/v1/admin/post/update")
    public ResponseWrapper<?> updatePost(@RequestBody ForumPost post) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        ForumPost existingPost = forumPostMapper.selectById(post.getId());
        if (existingPost != null) {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            if (post.getCategoryId() != null) {
                existingPost.setCategoryId(post.getCategoryId());
            }
            existingPost.setUpdatedTime(new Date());
            forumPostMapper.updateById(existingPost);
        }
        return new ResponseWrapper<>().success();
    }

    // ============================
    // 图书管理
    // ============================

    @Operation(summary = "图书管理列表")
    @GetMapping("/api/v1/admin/books")
    public ResponseWrapper<List<Book>> listBooks() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<Book>>) check;
        return new ResponseWrapper<List<Book>>().success(bookMapper.selectList(new QueryWrapper<>()));
    }

    @Operation(summary = "管理员编辑图书")
    @PostMapping("/api/v1/admin/book/update")
    public ResponseWrapper<?> updateBook(@RequestBody Book book) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        Book existing = bookMapper.selectById(book.getId());
        if (existing != null) {
            existing.setTitle(book.getTitle());
            existing.setAuthor(book.getAuthor());
            existing.setDescription(book.getDescription());
            if (book.getCoverImage() != null) {
                existing.setCoverImage(book.getCoverImage());
            }
            existing.setUpdatedTime(new Date());
            bookMapper.updateById(existing);
        }
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "管理员下架/上架图书")
    @PostMapping("/api/v1/admin/book/toggleStatus")
    public ResponseWrapper<?> toggleBookStatus(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        Book book = bookMapper.selectById(id);
        if (book != null) {
            // is_deleted: 0=上架, 1=下架
            book.setIsDeleted(book.getIsDeleted() != null && book.getIsDeleted() == 1 ? 0 : 1);
            book.setUpdatedTime(new Date());
            bookMapper.updateById(book);
        }
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "管理员物理删除图书（删除数据库记录和MinIO文件）")
    @PostMapping("/api/v1/admin/book/delete")
    public ResponseWrapper<?> deleteBook(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        Book book = bookMapper.selectById(id);
        if (book != null) {
            // 删除 MinIO 上的 PDF 文件
            if (book.getFileUrl() != null) {
                try {
                    // 从 URL 中提取 object name
                    String fileUrl = book.getFileUrl();
                    String objectName = extractObjectName(fileUrl);
                    if (objectName != null) {
                        minioFileDeleteService.deleteFile(objectName);
                    }
                } catch (Exception e) {
                    // 记录日志但继续删除数据库记录
                }
            }
            // 删除 MinIO 上的封面文件（如果有）
            if (book.getCoverImage() != null) {
                try {
                    String coverName = extractObjectName(book.getCoverImage());
                    if (coverName != null) {
                        minioFileDeleteService.deleteFile(coverName);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            // 物理删除数据库记录
            bookMapper.deleteById(id);
        }
        return new ResponseWrapper<>().success();
    }

    /** 从 MinIO URL 中提取 object name（bucket 之后的部分） */
    private String extractObjectName(String url) {
        if (url == null || url.isEmpty()) return null;
        // URL 格式类似 http://host:port/bucket/objectName
        int idx = url.indexOf("//");
        if (idx < 0) return url;
        String path = url.substring(idx + 2);
        int slashIdx = path.indexOf('/');
        if (slashIdx < 0) return null;
        return path.substring(slashIdx + 1);
    }

    // ============================
    // 标签管理
    // ============================

    @Operation(summary = "标签列表")
    @GetMapping("/api/v1/admin/tags")
    public ResponseWrapper<List<ArticleTag>> listTags() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<ArticleTag>>) check;
        return new ResponseWrapper<List<ArticleTag>>().success(articleTagMapper.selectList(new QueryWrapper<>()));
    }

    @Operation(summary = "新增标签")
    @PostMapping("/api/v1/admin/tag/save")
    public ResponseWrapper<?> saveTag(@RequestBody ArticleTag tag) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        articleTagMapper.insert(tag);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "删除标签")
    @PostMapping("/api/v1/admin/tag/delete")
    public ResponseWrapper<?> deleteTag(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        articleTagMapper.deleteById(id);
        return new ResponseWrapper<>().success();
    }

    // ============================
    // Skills 管理
    // ============================

    @Operation(summary = "Skills管理列表")
    @GetMapping("/api/v1/admin/skills")
    public ResponseWrapper<List<Skill>> listSkills() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<Skill>>) check;
        return new ResponseWrapper<List<Skill>>().success(skillMapper.selectList(new QueryWrapper<>()));
    }

    @Operation(summary = "管理员删除Skill")
    @PostMapping("/api/v1/admin/skill/delete")
    public ResponseWrapper<?> deleteSkill(@RequestParam Long id) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        skillMapper.deleteById(id);
        return new ResponseWrapper<>().success();
    }

    // ============================
    // 文档上传到 Milvus
    // ============================

    @Operation(summary = "上传文档并异步解析存入Milvus")
    @PostMapping(value = "/api/v1/admin/milvus/upload", consumes = "multipart/form-data")
    public ResponseWrapper<?> uploadToMilvus(@RequestParam("file") MultipartFile file) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        asyncDocumentService.parseAndStoreAsync(file);
        return new ResponseWrapper<>().success("文件已上传，后台正在解析文档，请稍后在向量检索中验证结果");
    }
}
