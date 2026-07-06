package cc.shiyi.coleditor.forum.table;

import cc.shiyi.coleditor.markdown.table.BaseTable;
import cc.shiyi.oss.http.MinioUrlSerializer;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("skill")
public class Skill extends BaseTable {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("content")
    private String content;

    @JsonSerialize(using = MinioUrlSerializer.class)
    @TableField("icon_url")
    private String iconUrl;

    @TableField("category")
    private String category;

    @TableField("author_id")
    private Long authorId;

    @TableField("download_count")
    private Integer downloadCount;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("status")
    private Integer status;

    @TableField("tags")
    private String tags;

    @JsonSerialize(using = MinioUrlSerializer.class)
    @TableField("attachment_url")
    private String attachmentUrl;

    @TableField("git_url")
    private String gitUrl;

}
