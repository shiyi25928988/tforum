package cc.shiyi.mcp.service;

import cc.shiyi.coleditor.common.ai.service.VectorService;
import cc.shiyi.mcp.dto.McpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
public class RagMcpService {

    @Autowired
    private VectorService vectorService;

    @Tool(description = "RAG检索：根据查询内容从向量数据库中搜索最相似的文档片段")
    public String searchSimilarDocuments(
            @ToolParam(description = "查询的文本内容") String query,
            @ToolParam(description = "返回结果数量，默认5") int topK) {
        log.info("RAG search query: {}, topK: {}", query, topK);
        List<Document> documents = vectorService.searchSimilarDocuments(query, topK);
        if (documents.isEmpty()) {
            return "未找到相关文档";
        }
        return documents.stream()
                .map(doc -> "【相关内容】" + doc.getFormattedContent())
                .collect(Collectors.joining("\n\n"));
    }

    @Tool(description = "存储文档到向量数据库，用于后续RAG检索")
    public String storeDocument(
            @ToolParam(description = "要存储的文本内容") String content,
            @ToolParam(description = "可选的元数据，JSON格式的key-value") Map<String, Object> metadata) {
        log.info("RAG store document, content length: {}", content.length());
        vectorService.storeDocument(content, metadata);
        return "文档已成功存储到向量数据库";
    }

    @Tool(description = "批量存储文档到向量数据库")
    public String storeDocuments(
            @ToolParam(description = "要存储的文本内容列表") List<String> contents) {
        log.info("RAG store documents, count: {}", contents.size());
        vectorService.storeDocuments(contents);
        return contents.size() + " 个文档已成功存储到向量数据库";
    }
}
