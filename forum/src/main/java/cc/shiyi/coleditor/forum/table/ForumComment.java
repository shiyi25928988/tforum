package cc.shiyi.coleditor.forum.table;

import cc.shiyi.coleditor.markdown.table.BaseTable;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("forum_comment")
public class ForumComment extends BaseTable {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("content")
    private String content;

    @TableField("parent_id")
    private Long parentId;

    @TableField("author_id")
    private Long authorId;

}
