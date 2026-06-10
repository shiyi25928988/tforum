package cc.shiyi.coleditor.forum.controller.search;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.SearchIndexService;
import cc.shiyi.search.model.IndexedDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "全局搜索-GlobalSearchController")
public class GlobalSearchController {

    private SearchIndexService searchIndexService;

    @Operation(summary = "全局模糊搜索")
    @GetMapping("/api/v1/search")
    public ResponseWrapper<List<IndexedDocument>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseWrapper<List<IndexedDocument>>().success(
                searchIndexService.search(keyword, pageNum, pageSize));
    }

}
