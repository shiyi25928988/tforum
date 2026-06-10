package cc.shiyi.coleditor.markdown.table;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class BaseTable {

    @TableField("created_time")
    private Date createdTime;

    @TableField("updated_time")
    private Date updatedTime;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("updater_id")
    private Long updaterId;

    @TableField("is_deleted")
    private Integer isDeleted;

}
