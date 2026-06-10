package cc.shiyi.coleditor.user.service;

import cc.shiyi.coleditor.user.mapper.AvatarMapper;
import cc.shiyi.coleditor.user.table.Avatar;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Setter(onMethod_ = @Autowired)
public class AvatarService {

    private AvatarMapper avatarMapper;

    public Avatar getAvatarById(Long id) {
        return avatarMapper.selectById(id);
    }

    public Avatar getRandomAvatar() {
        List<Avatar> list = avatarMapper.selectList(new QueryWrapper<>());
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }

    public List<Avatar> getAllAvatar() {
        return avatarMapper.selectList(new QueryWrapper<>());
    }
}
