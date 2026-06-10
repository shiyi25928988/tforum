package cc.shiyi.coleditor.forum.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("discussion_category")
public class DiscussionCategory {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("topic_count")
    private Integer topicCount;

}
