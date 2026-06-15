package cc.shiyi.coleditor.forum.controller;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.request.ForumCommentRequest;
import cc.shiyi.coleditor.forum.service.ForumCommentService;
import cc.shiyi.coleditor.forum.table.ForumComment;
import cc.shiyi.coleditor.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "论坛评论接口-ForumCommentController")
public class ForumCommentController {

    private ForumCommentService forumCommentService;
    private UserService userService;

    @Operation(summary = "根据帖子id获取评论列表")
    @GetMapping("/api/v1/forum/comment/{postId}")
    public ResponseWrapper<List<ForumComment>> listByPostId(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "commentType", required = false) String commentType) {
        return new ResponseWrapper<List<ForumComment>>().success(
                forumCommentService.listByPostIdAndType(postId, commentType));
    }

    @Operation(summary = "新增/更新评论")
    @PostMapping("/api/v1/forum/comment/save")
    public ResponseWrapper<ForumComment> save(@RequestBody ForumCommentRequest request) {
        ForumComment comment = new ForumComment();
        BeanUtils.copyProperties(request, comment);
        if (comment.getId() == null && userService.getCurrentUser() != null) {
            comment.setAuthorId(userService.getCurrentUser().getId());
        }
        return new ResponseWrapper<ForumComment>().success(forumCommentService.save(comment));
    }

    @Operation(summary = "删除评论")
    @PostMapping("/api/v1/forum/comment/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        forumCommentService.delete(id);
        return new ResponseWrapper<>().success();
    }

}
