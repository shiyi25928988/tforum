package cc.shiyi.coleditor.markdown.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("markdown_doc")
public class MarkdownDoc extends BaseTable {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("title")
    private String title;

    /** 完整的 Markdown 文本内容 */
    @TableField("content")
    private String content;

    @TableField("author_id")
    private Long authorId;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("tags")
    private String tags;

}
