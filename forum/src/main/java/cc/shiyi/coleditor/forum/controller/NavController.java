package cc.shiyi.coleditor.forum.controller;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.NavItemService;
import cc.shiyi.coleditor.forum.table.NavItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "导航栏-NavController")
public class NavController {

    private NavItemService navItemService;

    @Operation(summary = "获取可见的导航栏目列表")
    @GetMapping("/api/v1/nav/list")
    public ResponseWrapper<List<NavItem>> list() {
        return new ResponseWrapper<List<NavItem>>().success(navItemService.listVisible());
    }
}
