package cc.shiyi.coleditor.common.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
public class VectorService {

    @Autowired
    private VectorStore vectorStore;

    /**
     * 将字符串内容存储到向量数据库
     * @param content 要存储的文本内容
     * @param metadata 可选的元数据
     */
    public void storeDocument(String content, Map<String, Object> metadata) {
        // 创建文档对象
        Document document = new Document(content);

        // 添加元数据
        if (metadata != null) {
            document.getMetadata().putAll(metadata);
        }

        // 存储到向量数据库
        vectorStore.add(Arrays.asList(document));
    }

    /**
     * 简化版本 - 只存储文本内容
     * @param content 要存储的文本内容
     */
    public void storeDocument(String content) {
        storeDocument(content, null);
    }

    /**
     * 批量存储多个文档
     * @param contents 文档内容列表
     */
    public void storeDocuments(List<String> contents) {
        List<Document> documents = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            Document doc = new Document(contents.get(i));
            doc.getMetadata().put("index", i);
            doc.getMetadata().put("created_at", new Date());
            documents.add(doc);
        }

        vectorStore.add(documents);
    }

    /**
     * 根据查询内容进行相似性搜索
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 相似文档列表
     */
    public List<Document> searchSimilarDocuments(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(topK).build();
        return vectorStore.similaritySearch(searchRequest);
    }


}
