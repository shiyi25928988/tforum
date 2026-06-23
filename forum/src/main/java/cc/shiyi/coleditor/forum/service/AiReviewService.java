package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.common.ai.config.ChantClientPool;
import cc.shiyi.coleditor.forum.response.AiReviewResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 文章审核发布服务
 * 使用 AI 对文章内容进行质量审核，包括内容规范检查、可读性评估、改进建议等
 */
@Slf4j
@Service
@Setter(onMethod_ = @Autowired)
public class AiReviewService {

    private ChantClientPool chatClientPool;

    private static final String REVIEW_SYSTEM_PROMPT = """
            你是一位专业的技术文章审核编辑，负责审核即将发布到技术社区的文章。
            请按照以下标准对文章进行审核：

            1. **内容规范**：检查是否有不当言论、敏感信息、广告推广等违规内容
            2. **技术准确性**：评估技术内容的正确性和深度
            3. **可读性**：评估文章结构、排版、语言表达是否清晰易懂
            4. **完整性**：检查文章是否完整，是否有明显的未完成内容
            5. **原创性**：评估内容的原创程度

            请严格按照以下 JSON 格式返回审核结果（不要包含其他文字）：
            {
              "approved": true/false,
              "score": 1-10的整数,
              "feedback": "总体审核意见，100字以内",
              "suggestions": ["改进建议1", "改进建议2"],
              "issues": ["发现的问题1", "问题2"]
            }

            注意：
            - 如果存在明显违规内容（如广告、不当言论、与主题无关的灌水内容），应标记 approved=false 且 score<3
            - 如果内容质量较好，建议 approved=true
            - score 6分以上为合格，6分以下建议修改
            - issues 仅在发现问题时填写，suggestions 给出可操作的改进建议
            """;

    /**
     * 审核文章内容，返回审核结果
     */
    public AiReviewResponse review(String title, String content) {
        String conversationId = "review-" + UUID.randomUUID().toString().substring(0, 8);
        ChatClient chatClient = chatClientPool.get(conversationId);

        String userMessage = buildReviewPrompt(title, content);

        log.info("AiReviewService: reviewing article title={}, contentLength={}", title,
                content != null ? content.length() : 0);

        try {
            String response = chatClient.prompt()
                    .system(REVIEW_SYSTEM_PROMPT)
                    .user(userMessage)
                    .call()
                    .content();

            log.info("AiReviewService: raw response length={}", response != null ? response.length() : 0);
            return parseReviewResponse(response);
        } catch (Exception e) {
            log.error("AiReviewService: review failed", e);
            return AiReviewResponse.builder()
                    .approved(null)
                    .score(0)
                    .feedback("AI 审核服务异常: " + e.getMessage())
                    .suggestions(List.of("请稍后重试审核"))
                    .issues(List.of())
                    .build();
        }
    }

    /**
     * 流式审核 - 用于前端实时展示审核过程
     */
    public AiReviewResponse reviewStream(String title, String content) {
        // 流式场景下直接调用 review，ChatClient 内部处理流式
        return review(title, content);
    }

    private String buildReviewPrompt(String title, String content) {
        // 截取前 8000 字作为审核样本（防止超长文章超出 token 限制）
        String truncatedContent = content;
        if (content != null && content.length() > 8000) {
            truncatedContent = content.substring(0, 8000) + "\n\n...(文章过长，以上为前8000字内容)";
        }

        return String.format("""
                请审核以下文章：

                **标题**：%s

                **内容**：
                %s
                """, title != null ? title : "无标题", truncatedContent);
    }

    /**
     * 解析 AI 返回的 JSON 审核结果
     */
    private AiReviewResponse parseReviewResponse(String response) {
        if (response == null || response.isBlank()) {
            return AiReviewResponse.builder()
                    .approved(false)
                    .score(0)
                    .feedback("AI 未返回审核结果")
                    .suggestions(List.of())
                    .issues(List.of())
                    .build();
        }

        try {
            // 提取 JSON 部分（AI 可能在 JSON 前后加 markdown 代码块标记）
            String json = extractJson(response);

            // 手动解析关键字段（避免依赖 Jackson 的复杂对象映射）
            Boolean approved = extractBoolean(json, "approved");
            Integer score = extractInt(json, "score");
            String feedback = extractString(json, "feedback");
            List<String> suggestions = extractList(json, "suggestions");
            List<String> issues = extractList(json, "issues");

            return AiReviewResponse.builder()
                    .approved(approved)
                    .score(score != null ? score : 0)
                    .feedback(feedback != null ? feedback : "无审核意见")
                    .suggestions(suggestions != null ? suggestions : List.of())
                    .issues(issues != null ? issues : List.of())
                    .build();
        } catch (Exception e) {
            log.warn("AiReviewService: failed to parse response, returning raw as feedback", e);
            return AiReviewResponse.builder()
                    .approved(null)
                    .score(0)
                    .feedback("审核结果解析失败，原始返回: " + (response.length() > 200 ? response.substring(0, 200) + "..." : response))
                    .suggestions(List.of())
                    .issues(List.of())
                    .build();
        }
    }

    private String extractJson(String response) {
        // 去掉 ```json ... ``` 包裹
        Pattern p = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```");
        Matcher m = p.matcher(response);
        if (m.find()) {
            return m.group(1).trim();
        }
        // 尝试找到 { 开头 } 结尾的部分
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }

    private Boolean extractBoolean(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return null;
    }

    private Integer extractInt(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String extractString(String json, String key) {
        // 匹配 "key": "value" 中的 value（支持转义引号）
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return null;
    }

    private List<String> extractList(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[([\\s\\S]*?)\\]");
        Matcher m = p.matcher(json);
        if (m.find()) {
            String listContent = m.group(1);
            if (listContent.isBlank()) {
                return List.of();
            }
            // 提取每个引号包裹的字符串
            Pattern itemP = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"");
            Matcher itemM = itemP.matcher(listContent);
            List<String> items = new ArrayList<>();
            while (itemM.find()) {
                items.add(itemM.group(1).replace("\\\"", "\"").replace("\\\\", "\\"));
            }
            return items;
        }
        return List.of();
    }
}
