package cc.shiyi.coleditor.forum.controller.discussion;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.DiscussionCategoryService;
import cc.shiyi.coleditor.forum.table.DiscussionCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "讨论区分组管理-DiscussionCategoryController")
public class DiscussionCategoryController {

    private DiscussionCategoryService discussionCategoryService;

    @Operation(summary = "获取所有讨论分组")
    @GetMapping("/api/v1/discussion/category/list")
    public ResponseWrapper<List<DiscussionCategory>> list() {
        return new ResponseWrapper<List<DiscussionCategory>>().success(discussionCategoryService.listAll());
    }

    @Operation(summary = "新增分组")
    @PostMapping("/api/v1/discussion/category/save")
    public ResponseWrapper<DiscussionCategory> save(@RequestBody DiscussionCategory category) {
        return new ResponseWrapper<DiscussionCategory>().success(discussionCategoryService.save(category));
    }

    @Operation(summary = "删除分组")
    @PostMapping("/api/v1/discussion/category/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        discussionCategoryService.delete(id);
        return new ResponseWrapper<>().success();
    }
}
