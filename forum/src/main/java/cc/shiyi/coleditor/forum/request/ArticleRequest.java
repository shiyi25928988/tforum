package cc.shiyi.coleditor.forum.request;

import lombok.Data;

@Data
public class ArticleRequest {

    private Long id;

    private String title;

    private String content;

    private String summary;

    private String coverImage;

    private Long categoryId;

    /** 状态: 0=草稿, 1=已发布 */
    private Integer status;

    /** 是否置顶 */
    private Integer isPinned;

    /** 标签，逗号分隔 */
    private String tags;

}
