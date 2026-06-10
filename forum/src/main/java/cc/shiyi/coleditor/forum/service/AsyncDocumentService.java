package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.common.ai.document.DocumentLoader;
import cc.shiyi.coleditor.common.ai.service.VectorService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class AsyncDocumentService {

    @Autowired
    private DocumentLoader documentLoader;

    @Autowired
    private VectorService vectorService;

    @Async
    public void parseAndStoreAsync(MultipartFile file) {
        try {
            String name = file.getOriginalFilename();
            List<Document> documents;
            if (name != null && name.endsWith(".pdf")) {
                documents = documentLoader.loadPdf(file);
            } else {
                documents = documentLoader.load(file);
            }
            int count = 0;
            for (Document doc : documents) {
                vectorService.storeDocument(doc.getFormattedContent(), doc.getMetadata());
                count++;
            }
            log.info("异步解析完成: {} → {} 个文档片段已存入 Milvus", name, count);
        } catch (Exception e) {
            log.error("异步解析失败: {}", file.getOriginalFilename(), e);
        }
    }
}
