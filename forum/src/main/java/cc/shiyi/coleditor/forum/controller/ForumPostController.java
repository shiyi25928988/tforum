package cc.shiyi.coleditor.forum.controller;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.request.ForumPostRequest;
import cc.shiyi.coleditor.forum.service.ForumPostService;
import cc.shiyi.coleditor.forum.table.ForumPost;
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
@Tag(name = "论坛帖子接口-ForumPostController")
public class ForumPostController {

    private ForumPostService forumPostService;
    private UserService userService;

    @Operation(summary = "根据id获取帖子")
    @GetMapping("/api/v1/forum/post/{id}")
    public ResponseWrapper<ForumPost> getById(@PathVariable("id") Long id) {
        return new ResponseWrapper<ForumPost>().success(forumPostService.getById(id));
    }

    @Operation(summary = "分页获取帖子列表")
    @GetMapping("/api/v1/forum/post/list")
    public ResponseWrapper<Page<ForumPost>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId) {
        return new ResponseWrapper<Page<ForumPost>>().success(forumPostService.listByPage(pageNum, pageSize, categoryId));
    }

    @Operation(summary = "新增/更新帖子")
    @PostMapping("/api/v1/forum/post/save")
    public ResponseWrapper<ForumPost> save(@RequestBody ForumPostRequest request) {
        ForumPost post = new ForumPost();
        BeanUtils.copyProperties(request, post);
        if (post.getId() == null && userService.getCurrentUser() != null) {
            post.setAuthorId(userService.getCurrentUser().getId());
        }
        return new ResponseWrapper<ForumPost>().success(forumPostService.save(post));
    }

    @Operation(summary = "删除帖子")
    @PostMapping("/api/v1/forum/post/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        forumPostService.delete(id);
        return new ResponseWrapper<>().success();
    }

}
