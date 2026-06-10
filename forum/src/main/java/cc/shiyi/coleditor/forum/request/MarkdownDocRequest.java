package cc.shiyi.coleditor.forum.request;

import lombok.Data;

@Data
public class MarkdownDocRequest {

    private Long id;

    private String title;

    /** 完整的 Markdown 文本内容 */
    private String content;

    /** 标签，逗号分隔 */
    private String tags;

}
