package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.ArticleTagMapper;
import cc.shiyi.coleditor.forum.table.ArticleTag;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter(onMethod_ = @Autowired)
public class ArticleTagService {

    private ArticleTagMapper articleTagMapper;

    public List<ArticleTag> listAll() {
        QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        return articleTagMapper.selectList(queryWrapper);
    }

    public ArticleTag save(ArticleTag tag) {
        articleTagMapper.insert(tag);
        return tag;
    }

    public void delete(Long id) {
        articleTagMapper.deleteById(id);
    }

}
