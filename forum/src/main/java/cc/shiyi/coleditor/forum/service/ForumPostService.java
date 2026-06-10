package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.ForumPostMapper;
import cc.shiyi.coleditor.forum.table.ForumPost;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Setter(onMethod_ = @Autowired)
public class ForumPostService {

    private ForumPostMapper forumPostMapper;
    private SearchIndexService searchIndexService;

    public ForumPost save(ForumPost post) {
        Long id;
        if (Objects.isNull(post.getId()) || Objects.isNull(forumPostMapper.selectById(post.getId()))) {
            id = genNewId();
            post.setId(id);
            post.setCreatedTime(new Date());
            post.setViewCount(0);
            post.setCommentCount(0);
            forumPostMapper.insert(post);
        } else {
            id = post.getId();
            post.setUpdatedTime(new Date());
            forumPostMapper.updateById(post);
        }
        ForumPost result = getById(id);
        searchIndexService.index("forum", result.getId(), result.getTitle(), result.getContent());
        return result;
    }

    public ForumPost getById(Long id) {
        ForumPost post = forumPostMapper.selectById(id);
        if (Objects.nonNull(post)) {
            post.setViewCount(Objects.isNull(post.getViewCount()) ? 1 : post.getViewCount() + 1);
            forumPostMapper.updateById(post);
        }
        return post;
    }

    public void delete(Long id) {
        ForumPost post = forumPostMapper.selectById(id);
        if (Objects.nonNull(post)) {
            post.setIsDeleted(1);
            post.setUpdatedTime(new Date());
            forumPostMapper.updateById(post);
        }
    }

    public Page<ForumPost> listByPage(int pageNum, int pageSize, Long categoryId) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        if (Objects.nonNull(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }
        queryWrapper.orderByDesc("created_time");
        return forumPostMapper.selectPage(page, queryWrapper);
    }

    private Long genNewId() {
        if (Objects.isNull(forumPostMapper.maxId())) {
            return 1L;
        }
        return forumPostMapper.maxId() + 1L;
    }

}
