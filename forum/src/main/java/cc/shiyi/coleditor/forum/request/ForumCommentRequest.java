package cc.shiyi.coleditor.forum.request;

import lombok.Data;

@Data
public class ForumCommentRequest {

    private Long id;

    private Long postId;

    private String content;

    private Long parentId;

    private String commentType;

    private Long replyTo;

}
