package cc.shiyi.coleditor.forum.controller.markdown;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.request.MarkdownDocRequest;
import cc.shiyi.coleditor.forum.service.SearchIndexService;
import cc.shiyi.coleditor.markdown.service.MarkdownDocService;
import cc.shiyi.coleditor.markdown.table.MarkdownDoc;
import cc.shiyi.coleditor.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "Markdown 文档管理-MarkdownDocController")
public class MarkdownDocController {

    private MarkdownDocService markdownDocService;
    private UserService userService;
    private SearchIndexService searchIndexService;

    @Operation(summary = "根据id获取文档")
    @GetMapping("/api/v1/markdown/{id}")
    public ResponseWrapper<MarkdownDoc> getById(@PathVariable("id") Long id) {
        return new ResponseWrapper<MarkdownDoc>().success(markdownDocService.getById(id));
    }

    @Operation(summary = "分页获取文档列表")
    @GetMapping("/api/v1/markdown/list")
    public ResponseWrapper<Page<MarkdownDoc>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return new ResponseWrapper<Page<MarkdownDoc>>().success(
                markdownDocService.listByPage(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增/更新 Markdown 文档")
    @PostMapping("/api/v1/markdown/save")
    public ResponseWrapper<MarkdownDoc> save(@RequestBody MarkdownDocRequest request) {
        MarkdownDoc doc = new MarkdownDoc();
        BeanUtils.copyProperties(request, doc);
        if (doc.getId() == null && userService.getCurrentUser() != null) {
            doc.setAuthorId(userService.getCurrentUser().getId());
        }
        MarkdownDoc result = markdownDocService.save(doc);
        searchIndexService.index("markdown", result.getId(), result.getTitle(), result.getContent());
        return new ResponseWrapper<MarkdownDoc>().success(result);
    }

    @Operation(summary = "删除文档")
    @PostMapping("/api/v1/markdown/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        markdownDocService.delete(id);
        return new ResponseWrapper<>().success();
    }

}
