package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.NavItemMapper;
import cc.shiyi.coleditor.forum.table.NavItem;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NavItemService {

    @Autowired
    private NavItemMapper navItemMapper;

    /**
     * 获取所有可见的导航栏目，按 sort_order 升序排列
     */
    public List<NavItem> listVisible() {
        QueryWrapper<NavItem> qw = new QueryWrapper<>();
        qw.eq("is_visible", 1);
        qw.orderByAsc("sort_order");
        return navItemMapper.selectList(qw);
    }
}
