package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.ArticleMapper;
import cc.shiyi.coleditor.forum.table.Article;
import cc.shiyi.coleditor.user.service.UserService;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Setter(onMethod_ = @Autowired)
public class ArticleService {

    private ArticleMapper articleMapper;
    private SearchIndexService searchIndexService;
    private UserService userService;

    public Article save(Article article) {
        Long id;
        if (Objects.isNull(article.getId()) || Objects.isNull(articleMapper.selectById(article.getId()))) {
            id = genNewId();
            article.setId(id);
            article.setCreatedTime(new Date());
            article.setViewCount(0);
            article.setLikeCount(0);
            article.setCommentCount(0);
            if (Objects.isNull(article.getStatus())) {
                article.setStatus(1);
            }
            if (Objects.isNull(article.getIsPinned())) {
                article.setIsPinned(0);
            }
            articleMapper.insert(article);
        } else {
            id = article.getId();
            article.setUpdatedTime(new Date());
            articleMapper.updateById(article);
        }
        Article result = getById(id);
        searchIndexService.index("article", result.getId(), result.getTitle(), result.getContent());
        return result;
    }

    public Article getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (Objects.nonNull(article)) {
            article.setViewCount(Objects.isNull(article.getViewCount()) ? 1 : article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
        return article;
    }

    public void delete(Long id) {
        Article article = articleMapper.selectById(id);
        if (Objects.nonNull(article)) {
            article.setIsDeleted(1);
            article.setUpdatedTime(new Date());
            articleMapper.updateById(article);
        }
    }

    public Page<Article> listByPage(int pageNum, int pageSize, Long categoryId, String keyword) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        if (Objects.nonNull(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (Objects.nonNull(keyword) && !keyword.isEmpty()) {
            queryWrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        queryWrapper.orderByDesc("is_pinned", "created_time");
        return articleMapper.selectPage(page, queryWrapper);
    }

    public Page<Article> listPublishedByPage(int pageNum, int pageSize, Long categoryId) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.eq("status", 1);
        if (Objects.nonNull(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }
        queryWrapper.orderByDesc("is_pinned", "created_time");
        return articleMapper.selectPage(page, queryWrapper);
    }

    public Page<Article> listHot(int limit) {
        Page<Article> page = new Page<>(1, limit);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("view_count");
        return articleMapper.selectPage(page, queryWrapper);
    }

    public void like(Long id) {
        Article article = articleMapper.selectById(id);
        if (Objects.nonNull(article)) {
            article.setLikeCount(Objects.isNull(article.getLikeCount()) ? 1 : article.getLikeCount() + 1);
            articleMapper.updateById(article);
        }
    }

    private Long genNewId() {
        if (Objects.isNull(articleMapper.maxId())) {
            return 1L;
        }
        return articleMapper.maxId() + 1L;
    }

    /**
     * 获取当前用户自己的文章列表（含草稿和已发布）
     */
    public Page<Article> listMyArticles(int pageNum, int pageSize, Integer status) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        Long currentUserId = StpUtil.getLoginIdAsLong();
        queryWrapper.eq("author_id", currentUserId);
        if (Objects.nonNull(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("created_time");
        return articleMapper.selectPage(page, queryWrapper);
    }

    /**
     * 切换文章发布状态：草稿→发布 或 已发布→草稿
     */
    public void toggleStatus(Long id) {
        Article article = articleMapper.selectById(id);
        if (Objects.nonNull(article)) {
            // 校验是否是自己的文章
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!Objects.equals(article.getAuthorId(), currentUserId)) {
                throw new RuntimeException("无权操作他人的文章");
            }
            // 0=草稿→1=已发布, 1=已发布→0=草稿
            article.setStatus(article.getStatus() == 1 ? 0 : 1);
            article.setUpdatedTime(new Date());
            articleMapper.updateById(article);
        }
    }

}
