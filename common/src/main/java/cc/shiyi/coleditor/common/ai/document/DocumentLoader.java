package cc.shiyi.coleditor.common.ai.document;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DocumentLoader {

    @Autowired
    SemanticTextSplitter semanticTextSplitter;

    /**
     * 从上传的文件中加载文档内容
     *
     * @param file 要解析的上传文件
     * @return 包含文件内容和元数据的文档列表
     * @throws IOException 当文件读取或解析过程中发生IO错误时抛出
     * @throws TikaException 当Tika解析器遇到解析错误时抛出
     * @throws SAXException 当XML解析过程中发生错误时抛出
     */
    public List<Document> load(MultipartFile file) throws IOException, TikaException, SAXException {
        log.info("DocumentLoader load and parse file " + file.getOriginalFilename());
        List<Document> documents = new ArrayList<>();
        try(InputStream inputStream = file.getInputStream()) {
            ContentHandler contentHandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            org.apache.tika.parser.ParseContext parseContext = new org.apache.tika.parser.ParseContext();
            org.apache.tika.parser.Parser parser = new org.apache.tika.parser.AutoDetectParser();
            parser.parse(inputStream, contentHandler, metadata, parseContext);
            documents.addAll(semanticTextSplitter.split(new Document(contentHandler.toString(), Map.of("source", file.getOriginalFilename()))));
        }
        return documents;
    }

    /**
     * 加载PDF文件并将其内容转换为文档列表
     *
     * @param file 要加载的PDF文件，类型为MultipartFile
     * @return 包含PDF内容的Document对象列表
     * @throws IOException 当文件读取或转换过程中发生IO异常时抛出
     * @throws TikaException 当文档解析过程中发生Tika异常时抛出
     * @throws SAXException 当XML解析过程中发生SAX异常时抛出
     */
    public List<Document> loadPdf(MultipartFile file) throws IOException, TikaException, SAXException {
        log.info("DocumentLoader load and parse pdf file " + file.getOriginalFilename());
        List<Document> documents = new ArrayList<>();
        PagePdfDocumentReader reader = new PagePdfDocumentReader(new FileSystemResource(convert(file)));
        reader.get().forEach(document -> {
            documents.addAll(semanticTextSplitter.split(document));
        });
        return documents;
    }

    /**
     * 加载Markdown文件并解析为文档列表
     *
     * @param file 要加载的Markdown文件，类型为MultipartFile
     * @return 解析后的Document对象列表
     * @throws IOException 当文件读取或输入输出操作失败时抛出
     * @throws TikaException 当文件内容解析失败时抛出
     * @throws SAXException 当XML解析过程中出现错误时抛出
     */
    public List<Document> loadMarkdownFile(MultipartFile file) throws IOException, TikaException, SAXException {
         return loadMarkdownContent(file.getInputStream().toString(), Map.of("source", file.getOriginalFilename()));
    }

    /**
     * 加载并解析Markdown内容，将其转换为文档列表
     *
     * @param markdown 需要解析的Markdown格式字符串
     * @param metadata 附加的元数据信息，将被添加到解析后的文档中
     * @return 解析后的文档列表，每个文档都经过语义文本分割处理
     * @throws IOException 当读取或处理Markdown内容时发生I/O错误
     * @throws TikaException 当使用Tika解析文档时发生错误
     * @throws SAXException 当解析XML内容时发生错误
     */
    public List<Document> loadMarkdownContent(String markdown, Map<String, Object> metadata) throws IOException, TikaException, SAXException {
        List<Document> documents = new ArrayList<>();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(markdown, MarkdownDocumentReaderConfig.builder()
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withHorizontalRuleCreateDocument(true)
                .withAdditionalMetadata(metadata).build());
        reader.get().forEach(document -> {
            documents.addAll(semanticTextSplitter.split(document));
        });
        return documents;
    }

    public static File convert(MultipartFile multipartFile) throws IOException {
        Path tempPath = Files.createTempFile("upload_", multipartFile.getOriginalFilename());
        Files.copy(multipartFile.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);
        return tempPath.toFile();
    }
}
