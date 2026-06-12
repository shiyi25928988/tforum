package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.Skill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SkillMapper extends BaseMapper<Skill> {

    @Select("select MAX(id) from skill")
    Long maxId();
}
