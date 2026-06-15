package cc.shiyi.coleditor.forum.controller.admin;

import cc.shiyi.coleditor.common.ai.service.VectorService;
import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.AsyncDocumentService;
import cc.shiyi.coleditor.forum.mapper.ArticleMapper;
import cc.shiyi.coleditor.forum.mapper.ArticleTagMapper;
import cc.shiyi.coleditor.forum.mapper.ArticleVectorRecordMapper;
import cc.shiyi.coleditor.forum.mapper.BookMapper;
import cc.shiyi.coleditor.forum.mapper.ForumPostMapper;
import cc.shiyi.coleditor.forum.table.Article;
import cc.shiyi.coleditor.forum.table.ArticleTag;
import cc.shiyi.coleditor.forum.table.ArticleVectorRecord;
import cc.shiyi.coleditor.forum.table.Book;
import cc.shiyi.coleditor.forum.mapper.SkillMapper;
import cc.shiyi.coleditor.forum.table.Skill;
import cc.shiyi.coleditor.forum.table.ForumPost;
import cc.shiyi.coleditor.user.mapper.UserMapper;
import cc.shiyi.coleditor.user.table.User;
import cc.shiyi.oss.services.MinioFileDeleteService;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.milvus.client.MilvusClient;
import io.milvus.param.dml.DeleteParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private VectorService vectorService;
    private ArticleVectorRecordMapper articleVectorRecordMapper;
    private MilvusClient milvusClient;
    private Environment environment;

    private String getMilvusCollectionName() {
        return environment.getProperty("spring.ai.vectorstore.milvus.client.collection-name", "vector_store");
    }

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
            if ("admin".equals(user.getRole())) {
                return new ResponseWrapper<>().fail("不能禁用管理员账号");
            }
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
            if (book.getIsDeleted() != null && book.getIsDeleted() == 1) {
                bookMapper.recoverById(id);
            } else {
                bookMapper.deleteById(id);
            }
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
            if (book.getFileUrl() != null) {
                try {
                    String objectName = extractObjectName(book.getFileUrl());
                    if (objectName != null) {
                        minioFileDeleteService.deleteFile(objectName);
                    }
                } catch (Exception e) {}
            }
            if (book.getCoverImage() != null) {
                try {
                    String coverName = extractObjectName(book.getCoverImage());
                    if (coverName != null) {
                        minioFileDeleteService.deleteFile(coverName);
                    }
                } catch (Exception e) {}
            }
            bookMapper.deleteById(id);
        }
        return new ResponseWrapper<>().success();
    }

    private String extractObjectName(String url) {
        if (url == null || url.isEmpty()) return null;
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
    // Milvus 管理
    // ============================

    @Operation(summary = "上传文档并异步解析存入Milvus")
    @PostMapping(value = "/api/v1/admin/milvus/upload", consumes = "multipart/form-data")
    public ResponseWrapper<?> uploadToMilvus(@RequestParam("file") MultipartFile file) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        asyncDocumentService.parseAndStoreAsync(file);
        return new ResponseWrapper<>().success("文件已上传，后台正在解析文档，请稍后在向量检索中验证结果");
    }

    @Operation(summary = "将选中的文章存入向量数据库")
    @PostMapping("/api/v1/admin/milvus/storeArticles")
    public ResponseWrapper<?> storeArticles(@RequestBody List<Long> articleIds) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        int count = 0;
        for (Long articleId : articleIds) {
            // 先清理旧记录
            QueryWrapper<ArticleVectorRecord> delQw = new QueryWrapper<>();
            delQw.eq("article_id", articleId);
            articleVectorRecordMapper.delete(delQw);

            Article article = articleMapper.selectById(articleId);
            if (article == null || article.getStatus() == null || article.getStatus() != 1) {
                continue;
            }
            // 整篇文章作为一条向量记录
            String content = article.getTitle() + "\n" + (article.getContent() != null ? article.getContent() : "");
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("article_id", article.getId());
            metadata.put("title", article.getTitle());
            metadata.put("source", "article");
            vectorService.storeDocument(content, metadata);
            // 记录
            ArticleVectorRecord record = new ArticleVectorRecord();
            record.setArticleId(articleId);
            record.setCreatedTime(new Date());
            articleVectorRecordMapper.insert(record);
            count++;
        }
        return new ResponseWrapper<>().success("成功存入 " + count + " 篇文章到向量库");
    }

    @Operation(summary = "获取已存入向量库的文章ID列表")
    @GetMapping("/api/v1/admin/milvus/storedArticles")
    public ResponseWrapper<List<Long>> getStoredArticles() {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return (ResponseWrapper<List<Long>>) check;
        List<ArticleVectorRecord> records = articleVectorRecordMapper.selectList(new QueryWrapper<>());
        List<Long> ids = records.stream().map(ArticleVectorRecord::getArticleId).distinct().collect(java.util.stream.Collectors.toList());
        return new ResponseWrapper<List<Long>>().success(ids);
    }

    @Operation(summary = "从向量库中删除选中的文章")
    @PostMapping("/api/v1/admin/milvus/deleteArticles")
    public ResponseWrapper<?> deleteArticlesFromVector(@RequestBody List<Long> articleIds) {
        ResponseWrapper<?> check = checkAdmin();
        if (check != null) return check;
        // 通过 metadata JSON 字段中的 article_id 删除
        try {
            String expr = articleIds.stream()
                    .map(id -> "metadata[\"article_id\"] == " + id)
                    .collect(java.util.stream.Collectors.joining(" or "));
            milvusClient.delete(DeleteParam.newBuilder()
                    .withCollectionName(getMilvusCollectionName())
                    .withExpr(expr)
                    .build());
        } catch (Exception e) {
            return new ResponseWrapper<>().fail("向量删除失败: " + e.getMessage());
        }
        // 删除数据库记录
        for (Long articleId : articleIds) {
            QueryWrapper<ArticleVectorRecord> qw = new QueryWrapper<>();
            qw.eq("article_id", articleId);
            articleVectorRecordMapper.delete(qw);
        }
        return new ResponseWrapper<>().success("已从向量库删除 " + articleIds.size() + " 篇文章");
    }
}
