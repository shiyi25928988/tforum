package cc.shiyi.mcp.service;

import cc.shiyi.search.model.IndexedDocument;
import cc.shiyi.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
public class SearchMcpService {

    @Autowired
    private SearchService searchService;

    @Tool(description = "全文搜索：根据关键词在论坛文章、帖子和文档中进行模糊搜索，返回匹配结果")
    public String fuzzySearch(
            @ToolParam(description = "搜索关键词") String query,
            @ToolParam(description = "页码，从1开始，默认1") int pageNum,
            @ToolParam(description = "每页大小，默认10") int pageSize) {
        log.info("SearchMcpService fuzzySearch: query={}, pageNum={}, pageSize={}", query, pageNum, pageSize);
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 50) pageSize = 10;

        List<IndexedDocument> results = searchService.fuzzySearch(query, pageNum, pageSize);
        if (results == null || results.isEmpty()) {
            return "未找到与 '" + query + "' 相关的内容";
        }

        return results.stream()
                .map(doc -> String.format(
                        "【%s】\n  URL: %s\n  时间: %s\n  摘要: %s",
                        doc.getTitle(),
                        doc.getUrl(),
                        doc.getCreateTime() != null ? doc.getCreateTime().toString() : "未知",
                        truncate(doc.getContent(), 200)
                ))
                .collect(Collectors.joining("\n\n"));
    }

    @Tool(description = "索引文档：将文档添加到全文搜索引擎中，使其可被搜索")
    public String addDocument(
            @ToolParam(description = "文档ID") Long id,
            @ToolParam(description = "文档标题") String title,
            @ToolParam(description = "文档内容") String content,
            @ToolParam(description = "文档URL") String url) {
        log.info("SearchMcpService addDocument: id={}, title={}", id, title);
        IndexedDocument doc = new IndexedDocument();
        doc.setId(id);
        doc.setTitle(title);
        doc.setContent(content);
        doc.setUrl(url);
        doc.setCreateTime(new java.util.Date());
        searchService.addDocuments(List.of(doc));
        return "文档 '" + title + "' 已成功索引";
    }

    @Tool(description = "清空搜索索引：删除所有已索引的文档，谨慎使用")
    public String clearIndex() {
        log.info("SearchMcpService clearIndex");
        searchService.clearAll();
        return "搜索索引已清空";
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "...";
    }
}
