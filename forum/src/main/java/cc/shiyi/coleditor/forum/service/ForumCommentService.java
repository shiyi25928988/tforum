package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.ForumCommentMapper;
import cc.shiyi.coleditor.forum.mapper.ForumPostMapper;
import cc.shiyi.coleditor.forum.table.ForumComment;
import cc.shiyi.coleditor.forum.table.ForumPost;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Setter(onMethod_ = @Autowired)
public class ForumCommentService {

    private ForumCommentMapper forumCommentMapper;
    private ForumPostMapper forumPostMapper;

    public ForumComment save(ForumComment comment) {
        Long id;
        if (Objects.isNull(comment.getId()) || Objects.isNull(forumCommentMapper.selectById(comment.getId()))) {
            id = genNewId();
            comment.setId(id);
            comment.setCreatedTime(new Date());
            forumCommentMapper.insert(comment);
            updatePostCommentCount(comment.getPostId());
        } else {
            id = comment.getId();
            comment.setUpdatedTime(new Date());
            forumCommentMapper.updateById(comment);
        }
        return getById(id);
    }

    public ForumComment getById(Long id) {
        return forumCommentMapper.selectById(id);
    }

    public void delete(Long id) {
        ForumComment comment = forumCommentMapper.selectById(id);
        if (Objects.nonNull(comment)) {
            comment.setIsDeleted(1);
            comment.setUpdatedTime(new Date());
            forumCommentMapper.updateById(comment);
            updatePostCommentCount(comment.getPostId());
        }
    }

    public List<ForumComment> listByPostId(Long postId) {
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByAsc("created_time");
        return forumCommentMapper.selectList(queryWrapper);
    }

    private void updatePostCommentCount(Long postId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (Objects.nonNull(post)) {
            QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id", postId);
            queryWrapper.eq("is_deleted", 0);
            post.setCommentCount(Math.toIntExact(forumCommentMapper.selectCount(queryWrapper)));
            forumPostMapper.updateById(post);
        }
    }

    private Long genNewId() {
        if (Objects.isNull(forumCommentMapper.maxId())) {
            return 1L;
        }
        return forumCommentMapper.maxId() + 1L;
    }

}
