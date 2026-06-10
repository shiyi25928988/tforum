package cc.shiyi.coleditor.forum.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("article_tag")
public class ArticleTag {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", fill = FieldFill.INSERT)
    private Long id;

    @TableField("name")
    private String name;

}
