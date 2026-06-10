package cc.shiyi.coleditor.forum.service;

import cc.shiyi.search.model.IndexedDocument;
import cc.shiyi.search.service.SearchService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 论坛全局搜索索引服务
 * 在文章/帖子/Markdown 文档保存时同步更新 Lucene 索引
 */
@Service
@Setter(onMethod_ = @Autowired)
public class SearchIndexService {

    private SearchService searchService;

    /** 索引单条内容 */
    public void index(String type, Long id, String title, String content) {
        IndexedDocument doc = new IndexedDocument();
        doc.setId(id);
        doc.setTitle(title);
        doc.setContent(content);
        doc.setUrl("/" + type + "/" + id);
        doc.setCreateTime(new java.util.Date());
        searchService.addDocuments(List.of(doc));
    }

    /** 搜索全部内容 */
    public List<IndexedDocument> search(String keyword, int pageNum, int pageSize) {
        return searchService.fuzzySearch(keyword, pageNum, pageSize);
    }

    /** 重建全部索引 */
    public void rebuild() {
        searchService.clearAll();
    }

}
