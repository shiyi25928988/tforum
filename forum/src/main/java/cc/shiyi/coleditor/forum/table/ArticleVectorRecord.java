package cc.shiyi.coleditor.forum.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("article_vector_record")
public class ArticleVectorRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("article_id")
    private Long articleId;

    @TableField("milvus_id")
    private Long milvusId;

    @TableField("created_time")
    private Date createdTime;

}
