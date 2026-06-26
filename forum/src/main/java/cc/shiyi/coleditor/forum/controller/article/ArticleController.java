package cc.shiyi.coleditor.forum.controller.article;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.request.ArticleRequest;
import cc.shiyi.coleditor.forum.response.AiReviewResponse;
import cc.shiyi.coleditor.forum.response.AiReviewTaskResponse;
import cc.shiyi.coleditor.forum.service.AiReviewService;
import cc.shiyi.coleditor.forum.service.ArticleService;
import cc.shiyi.coleditor.forum.table.Article;
import cc.shiyi.coleditor.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "文章管理-ArticleController")
public class ArticleController {

    private ArticleService articleService;
    private UserService userService;
    private AiReviewService aiReviewService;

    @Operation(summary = "根据id获取文章")
    @GetMapping("/api/v1/article/{id}")
    public ResponseWrapper<Article> getById(@PathVariable("id") Long id) {
        return new ResponseWrapper<Article>().success(articleService.getById(id));
    }

    @Operation(summary = "分页获取已发布文章列表")
    @GetMapping("/api/v1/article/list")
    public ResponseWrapper<Page<Article>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId) {
        return new ResponseWrapper<Page<Article>>().success(
                articleService.listPublishedByPage(pageNum, pageSize, categoryId));
    }

    @Operation(summary = "分页搜索文章（含关键词）")
    @GetMapping("/api/v1/article/search")
    public ResponseWrapper<Page<Article>> search(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return new ResponseWrapper<Page<Article>>().success(
                articleService.listByPage(pageNum, pageSize, categoryId, keyword));
    }

    @Operation(summary = "新增/更新文章")
    @PostMapping("/api/v1/article/save")
    public ResponseWrapper<Article> save(@RequestBody ArticleRequest request) {
        Article article = new Article();
        BeanUtils.copyProperties(request, article);
        if (article.getId() == null && userService.getCurrentUser() != null) {
            article.setAuthorId(userService.getCurrentUser().getId());
        }
        return new ResponseWrapper<Article>().success(articleService.save(article));
    }

    @Operation(summary = "删除文章")
    @PostMapping("/api/v1/article/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        articleService.delete(id);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "点赞文章")
    @PostMapping("/api/v1/article/like")
    public ResponseWrapper<?> like(@RequestParam Long id) {
        articleService.like(id);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "获取当前用户的文章列表（含草稿和已发布）")
    @GetMapping("/api/v1/article/my")
    public ResponseWrapper<Page<Article>> myArticles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return new ResponseWrapper<Page<Article>>().success(
                articleService.listMyArticles(pageNum, pageSize, status));
    }

    @Operation(summary = "切换文章发布状态")
    @PostMapping("/api/v1/article/toggleStatus")
    public ResponseWrapper<?> toggleStatus(@RequestParam Long id) {
        articleService.toggleStatus(id);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "获取热门文章（按浏览量排序）")
    @GetMapping("/api/v1/article/hot")
    public ResponseWrapper<Page<Article>> hot(
            @RequestParam(defaultValue = "10") int limit) {
        return new ResponseWrapper<Page<Article>>().success(
                articleService.listHot(limit));
    }

    @Operation(summary = "提交 AI 审核任务（异步），返回任务 ID 用于轮询")
    @PostMapping("/api/v1/article/review")
    public ResponseWrapper<AiReviewTaskResponse> review(@RequestBody ArticleRequest request) {
        return new ResponseWrapper<AiReviewTaskResponse>().success(
                aiReviewService.submit(request.getTitle(), request.getContent()));
    }

    @Operation(summary = "轮询 AI 审核结果，未完成时返回 null data")
    @GetMapping("/api/v1/article/review/result")
    public ResponseWrapper<AiReviewResponse> reviewResult(@RequestParam String taskId) {
        AiReviewResponse result = aiReviewService.getResult(taskId);
        if (result == null) {
            // 返回 code=0, data=null 表示任务还未完成，前端继续轮询
            return new ResponseWrapper<AiReviewResponse>().success();
        }
        return new ResponseWrapper<AiReviewResponse>().success(result);
    }

}
