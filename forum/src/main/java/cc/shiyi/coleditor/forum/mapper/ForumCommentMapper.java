package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.ForumComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {

    @Select("select MAX(id) from forum_comment")
    Long maxId();
}
