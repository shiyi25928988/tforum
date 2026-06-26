package cc.shiyi.coleditor.forum.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 审核任务提交响应，包含任务 ID 用于后续轮询
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiReviewTaskResponse {

    /** 任务 ID，前端用于轮询结果 */
    private String taskId;

    /** 状态：PENDING */
    private String status;

}
