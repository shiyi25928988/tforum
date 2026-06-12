package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.SkillMapper;
import cc.shiyi.coleditor.forum.table.Skill;
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
public class SkillService {

    private SkillMapper skillMapper;

    public Skill save(Skill skill) {
        if (Objects.isNull(skill.getId()) || Objects.isNull(skillMapper.selectById(skill.getId()))) {
            skill.setId(genNewId());
            skill.setCreatedTime(new Date());
            skill.setDownloadCount(0);
            skill.setViewCount(0);
            if (Objects.isNull(skill.getStatus())) {
                skill.setStatus(1);
            }
            skillMapper.insert(skill);
        } else {
            skill.setUpdatedTime(new Date());
            skillMapper.updateById(skill);
        }
        return getById(skill.getId());
    }

    public Skill getById(Long id) {
        Skill skill = skillMapper.selectById(id);
        if (Objects.nonNull(skill)) {
            skill.setViewCount(Objects.isNull(skill.getViewCount()) ? 1 : skill.getViewCount() + 1);
            skillMapper.updateById(skill);
        }
        return skill;
    }

    public void delete(Long id) {
        Skill skill = skillMapper.selectById(id);
        if (Objects.nonNull(skill)) {
            skillMapper.deleteById(id);
        }
    }

    public Page<Skill> listByPage(int pageNum, int pageSize, String category, String keyword) {
        Page<Skill> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Skill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        if (Objects.nonNull(category) && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        if (Objects.nonNull(keyword) && !keyword.isEmpty()) {
            queryWrapper.and(w -> w.like("name", keyword).or().like("description", keyword).or().like("tags", keyword));
        }
        queryWrapper.orderByDesc("created_time");
        return skillMapper.selectPage(page, queryWrapper);
    }

    public Page<Skill> listMySkills(int pageNum, int pageSize) {
        Page<Skill> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Skill> queryWrapper = new QueryWrapper<>();
        Long currentUserId = StpUtil.getLoginIdAsLong();
        queryWrapper.eq("author_id", currentUserId);
        queryWrapper.orderByDesc("created_time");
        return skillMapper.selectPage(page, queryWrapper);
    }

    public void increaseDownload(Long id) {
        Skill skill = skillMapper.selectById(id);
        if (Objects.nonNull(skill)) {
            skill.setDownloadCount(Objects.isNull(skill.getDownloadCount()) ? 1 : skill.getDownloadCount() + 1);
            skillMapper.updateById(skill);
        }
    }

    private Long genNewId() {
        if (Objects.isNull(skillMapper.maxId())) {
            return 1L;
        }
        return skillMapper.maxId() + 1L;
    }
}
