package cc.shiyi.coleditor.user.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName(value = "avatar")
public class Avatar {

    @TableField(value = "id", fill = FieldFill.INSERT)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "svg")
    private String svg;
}
