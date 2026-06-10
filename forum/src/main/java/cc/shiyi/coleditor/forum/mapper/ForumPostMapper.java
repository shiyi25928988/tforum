package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.ForumPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

    @Select("select MAX(id) from forum_post")
    Long maxId();
}
