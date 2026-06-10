package cc.shiyi.search.service;

import cc.shiyi.search.model.IndexedDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Value("${lucene.storage.path}")
    String storagePath;

    public void addDocuments(List<IndexedDocument> indexedDocuments) {
        try (Indexer indexer = new Indexer(storagePath)) {
            for (IndexedDocument indexedDocument : indexedDocuments) {
                indexer.addDocument(indexedDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<IndexedDocument> fuzzySearch(String query, int pageNum, int pageSize) {
        try (Querier querier = new Querier(storagePath)) {
            return querier.fuzzySearch(query, pageNum, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearAll(){
        try (Indexer indexer = new Indexer(storagePath)) {
            indexer.clearAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
