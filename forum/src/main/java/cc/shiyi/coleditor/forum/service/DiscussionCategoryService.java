package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.DiscussionCategoryMapper;
import cc.shiyi.coleditor.forum.mapper.ForumPostMapper;
import cc.shiyi.coleditor.forum.table.DiscussionCategory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter(onMethod_ = @Autowired)
public class DiscussionCategoryService {

    private DiscussionCategoryMapper discussionCategoryMapper;
    private ForumPostMapper forumPostMapper;

    public List<DiscussionCategory> listAll() {
        QueryWrapper<DiscussionCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order");
        List<DiscussionCategory> categories = discussionCategoryMapper.selectList(queryWrapper);

        // 动态统计每个分组的话题数
        for (DiscussionCategory cat : categories) {
            QueryWrapper<cc.shiyi.coleditor.forum.table.ForumPost> countWrapper = new QueryWrapper<>();
            countWrapper.eq("category_id", cat.getId());
            countWrapper.eq("is_deleted", 0);
            cat.setTopicCount(Math.toIntExact(forumPostMapper.selectCount(countWrapper)));
        }
        return categories;
    }

    public DiscussionCategory save(DiscussionCategory category) {
        discussionCategoryMapper.insert(category);
        return category;
    }

    public void delete(Long id) {
        discussionCategoryMapper.deleteById(id);
    }
}
