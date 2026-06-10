package cc.shiyi.search.service;

import cc.shiyi.search.model.IndexedDocument;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

public class Indexer implements AutoCloseable{

    private final Directory directory;

    private final Analyzer analyzer;

    private IndexWriter indexWriter;

    public Indexer(String storagePath) throws IOException {
        this.directory = FSDirectory.open(Paths.get(storagePath));
        this.analyzer = new IKAnalyzer(true);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        this.indexWriter = new IndexWriter(directory, indexWriterConfig);
    }

    public void addDocument(IndexedDocument indexedDocument) throws IOException {
        Document document =  new Document();
        document.add(new StringField("id", String.valueOf(indexedDocument.getId()), StringField.Store.YES));
        document.add(new StringField("title", indexedDocument.getTitle(), StringField.Store.YES));
        document.add(new TextField("content", indexedDocument.getContent(), StringField.Store.YES));
        document.add(new StringField("url", indexedDocument.getUrl(), StringField.Store.YES));
        document.add(new LongField("createTime", indexedDocument.getCreateTime().getTime(), LongField.Store.YES));
        indexWriter.addDocument(document);
    }

    public void clearAll() throws IOException {
        indexWriter.deleteAll();
    }

    @Override
    public void close() throws IOException {
        indexWriter.close();
    }

}
