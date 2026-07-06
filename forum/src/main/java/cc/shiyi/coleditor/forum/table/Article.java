package cc.shiyi.coleditor.forum.table;

import cc.shiyi.coleditor.markdown.table.BaseTable;
import cc.shiyi.oss.http.MinioUrlSerializer;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseTable {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("summary")
    private String summary;

    @JsonSerialize(using = MinioUrlSerializer.class)
    @TableField("cover_image")
    private String coverImage;

    @TableField("category_id")
    private Long categoryId;

    @TableField("author_id")
    private Long authorId;

    /** 状态: 0=草稿, 1=已发布 */
    @TableField("status")
    private Integer status;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("comment_count")
    private Integer commentCount;

    /** 是否置顶 */
    @TableField("is_pinned")
    private Integer isPinned;

    @TableField("tags")
    private String tags;

}
