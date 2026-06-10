package cc.shiyi.coleditor.forum.controller.article;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.ArticleTagService;
import cc.shiyi.coleditor.forum.table.ArticleTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "文章标签管理-ArticleTagController")
public class ArticleTagController {

    private ArticleTagService articleTagService;

    @Operation(summary = "获取所有标签")
    @GetMapping("/api/v1/article/tag/list")
    public ResponseWrapper<List<ArticleTag>> list() {
        return new ResponseWrapper<List<ArticleTag>>().success(articleTagService.listAll());
    }

    @Operation(summary = "新增标签")
    @PostMapping("/api/v1/article/tag/save")
    public ResponseWrapper<ArticleTag> save(@RequestBody ArticleTag tag) {
        return new ResponseWrapper<ArticleTag>().success(articleTagService.save(tag));
    }

    @Operation(summary = "删除标签")
    @PostMapping("/api/v1/article/tag/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        articleTagService.delete(id);
        return new ResponseWrapper<>().success();
    }

}
