package cc.shiyi.coleditor.forum.controller.ai;

import cc.shiyi.coleditor.common.ai.document.DocumentLoader;
import cc.shiyi.coleditor.common.ai.document.DocumentRequest;
import cc.shiyi.coleditor.common.ai.service.VectorService;
import cc.shiyi.coleditor.common.http.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.apache.tika.exception.TikaException;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "向量文档控制-VectorDocumentController")
public class VectorDocumentController {

    DocumentLoader documentLoader;
    VectorService vectorService;

    @Operation(summary = "上传文档文件并储存到Milvus")
    @PostMapping(value = "/api/v1/vector/uploadAndStoreDocumentFile", consumes = "multipart/form-data")
    public ResponseWrapper<?> uploadDocumentFile(@RequestParam("file") MultipartFile file) throws TikaException, IOException, SAXException {
        List<Document> documents;
        if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".pdf")) {
            documents = documentLoader.loadPdf(file);
        } else {
            documents = documentLoader.load(file);
        }
        if (!documents.isEmpty()) {
            documents.forEach(document -> vectorService.storeDocument(document.getFormattedContent(), document.getMetadata()));
        }
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "存储单个文档到向量数据库中")
    @PostMapping("/api/v1/vector/storeDocument")
    public ResponseWrapper<?> storeDocument(@RequestBody DocumentRequest request) {
        try {
            vectorService.storeDocument(request.getContent(), request.getMetadata());
            return new ResponseWrapper<>().success("succeed to store document");
        } catch (Exception e) {
            return new ResponseWrapper<>().fail(e, e.getMessage());
        }
    }

    @Operation(summary = "储存多条文档到Milvus")
    @PostMapping("/api/v1/vector/storeDocuments")
    public ResponseWrapper<?> storeDocuments(@RequestBody List<String> contents) {
        try {
            vectorService.storeDocuments(contents);
            return new ResponseWrapper<>().success("succeed to store documents");
        } catch (Exception e) {
            return new ResponseWrapper<>().fail(e, e.getMessage());
        }
    }

    @Operation(summary = "通过milvus做相似性检索")
    @GetMapping("/api/v1/vector/similarSearch")
    public List<Document> similarSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        return vectorService.searchSimilarDocuments(query, topK);
    }

}
