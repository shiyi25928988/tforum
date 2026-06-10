package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select MAX(id) from article")
    Long maxId();
}
