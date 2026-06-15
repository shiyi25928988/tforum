package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.ForumPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

    @Select("select MAX(id) from forum_post")
    Long maxId();

    @Update("UPDATE forum_post SET comment_count = #{count} WHERE id = #{id}")
    void updateCommentCount(@Param("id") Long id, @Param("count") int count);
}
