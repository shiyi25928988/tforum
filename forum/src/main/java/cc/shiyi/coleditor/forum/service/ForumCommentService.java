package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.ArticleMapper;
import cc.shiyi.coleditor.forum.mapper.ForumCommentMapper;
import cc.shiyi.coleditor.forum.mapper.ForumPostMapper;
import cc.shiyi.coleditor.forum.table.ForumComment;
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
    private ArticleMapper articleMapper;

    public ForumComment save(ForumComment comment) {
        Long id;
        if (Objects.isNull(comment.getId()) || Objects.isNull(forumCommentMapper.selectById(comment.getId()))) {
            id = genNewId();
            comment.setId(id);
            comment.setCreatedTime(new Date());
            forumCommentMapper.insert(comment);
            updateCommentCount(comment.getPostId(), comment.getCommentType());
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
            forumCommentMapper.deleteById(id);
            updateCommentCount(comment.getPostId(), comment.getCommentType());
        }
    }

    public List<ForumComment> listByPostId(Long postId) {
        return listByPostIdAndType(postId, null);
    }

    public List<ForumComment> listByPostIdAndType(Long postId, String commentType) {
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        queryWrapper.eq("is_deleted", 0);
        if (commentType != null && !commentType.isEmpty()) {
            queryWrapper.eq("comment_type", commentType);
        }
        queryWrapper.orderByAsc("created_time");
        return forumCommentMapper.selectList(queryWrapper);
    }

    private void updateCommentCount(Long postId, String commentType) {
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        queryWrapper.eq("is_deleted", 0);
        int count = Math.toIntExact(forumCommentMapper.selectCount(queryWrapper));

        if ("article".equals(commentType)) {
            articleMapper.updateCommentCount(postId, count);
        } else {
            forumPostMapper.updateCommentCount(postId, count);
        }
    }

    private Long genNewId() {
        if (Objects.isNull(forumCommentMapper.maxId())) {
            return 1L;
        }
        return forumCommentMapper.maxId() + 1L;
    }

}
