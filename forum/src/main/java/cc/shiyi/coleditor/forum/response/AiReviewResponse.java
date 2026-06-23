package cc.shiyi.coleditor.forum.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 审核文章结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiReviewResponse {

    /** 是否通过审核 */
    private Boolean approved;

    /** 评分 1-10 */
    private Integer score;

    /** 审核意见 */
    private String feedback;

    /** 改进建议 */
    private List<String> suggestions;

    /** 发现的问题 */
    private List<String> issues;

}
