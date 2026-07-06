package cc.shiyi.coleditor.forum.table;

import cc.shiyi.coleditor.markdown.table.BaseTable;
import cc.shiyi.oss.http.MinioUrlSerializer;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("book")
public class Book extends BaseTable {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("author")
    private String author;

    @TableField("description")
    private String description;

    @JsonSerialize(using = MinioUrlSerializer.class)
    @TableField("cover_image")
    private String coverImage;

    @JsonSerialize(using = MinioUrlSerializer.class)
    @TableField("file_url")
    private String fileUrl;

    @TableField("file_size")
    private Long fileSize;

    @TableField("category_id")
    private Long categoryId;

    @TableField("uploader_id")
    private Long uploaderId;

    @TableField("download_count")
    private Integer downloadCount;

    @TableField("view_count")
    private Integer viewCount;

    /** PDF 文件 SHA-256 哈希值，用于防重复 */
    @TableField("file_hash")
    private String fileHash;

}
