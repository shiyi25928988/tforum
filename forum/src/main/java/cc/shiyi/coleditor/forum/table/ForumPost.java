package cc.shiyi.coleditor.forum.table;

import cc.shiyi.coleditor.markdown.table.BaseTable;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("forum_post")
public class ForumPost extends BaseTable {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("category_id")
    private Long categoryId;

    @TableField("author_id")
    private Long authorId;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("comment_count")
    private Integer commentCount;

}
