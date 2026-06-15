package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select MAX(id) from article")
    Long maxId();

    @Update("UPDATE article SET comment_count = #{count} WHERE id = #{id}")
    void updateCommentCount(@Param("id") Long id, @Param("count") int count);
}
