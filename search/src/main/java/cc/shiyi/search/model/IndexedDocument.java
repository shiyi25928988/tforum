package cc.shiyi.search.model;

import lombok.Data;

import java.util.Date;

@Data
public class IndexedDocument {
    Long id;
    String title;
    String content;
    String url;
    Date createTime;
}
