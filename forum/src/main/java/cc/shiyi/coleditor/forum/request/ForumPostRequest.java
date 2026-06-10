package cc.shiyi.coleditor.forum.request;

import lombok.Data;

@Data
public class ForumPostRequest {

    private Long id;

    private String title;

    private String content;

    private Long categoryId;

}
