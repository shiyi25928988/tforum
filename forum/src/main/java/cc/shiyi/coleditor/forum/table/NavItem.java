package cc.shiyi.coleditor.forum.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 导航栏配置表。
 * 系统内置栏目（首页/讨论区/Skills/图书角）is_system=1，不可删除但可切换显隐（首页除外）。
 * 管理员自定义栏目 is_system=0，支持完整 CRUD，可配置为外部链接（type=external）跳转到其他 host。
 */
@Data
@TableName("nav_item")
public class NavItem {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    /** 栏目名称 */
    @TableField("name")
    private String name;

    /** 链接 URL（内部路由或外部完整链接） */
    @TableField("url")
    private String url;

    /** 图标（可选） */
    @TableField("icon")
    private String icon;

    /** 类型: internal=内部路由, external=外部链接 */
    @TableField("type")
    private String type;

    /** 是否显示 */
    @TableField("is_visible")
    private Integer isVisible;

    /** 排序（越小越靠前） */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 是否系统内置: 1=系统内置, 0=自定义 */
    @TableField("is_system")
    private Integer isSystem;
}
