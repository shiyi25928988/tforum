package cc.shiyi.coleditor.forum.controller.skill;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.service.SkillService;
import cc.shiyi.coleditor.forum.table.Skill;
import cc.shiyi.coleditor.user.service.UserService;
import cc.shiyi.oss.common.UploadService;
import cc.shiyi.oss.utils.MinioUrlUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "Skills管理-SkillController")
public class SkillController {

    private SkillService skillService;
    private UserService userService;
    private UploadService uploadService;

    @Operation(summary = "获取Skill详情")
    @GetMapping("/api/v1/skill/{id}")
    public ResponseWrapper<Skill> getById(@PathVariable("id") Long id) {
        return new ResponseWrapper<Skill>().success(skillService.getById(id));
    }

    @Operation(summary = "分页获取Skills列表")
    @GetMapping("/api/v1/skill/list")
    public ResponseWrapper<Page<Skill>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return new ResponseWrapper<Page<Skill>>().success(
                skillService.listByPage(pageNum, pageSize, category, keyword));
    }

    @Operation(summary = "获取当前用户的Skills")
    @GetMapping("/api/v1/skill/my")
    public ResponseWrapper<Page<Skill>> mySkills(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseWrapper<Page<Skill>>().success(
                skillService.listMySkills(pageNum, pageSize));
    }

    @Operation(summary = "发布/更新Skill")
    @PostMapping(value = "/api/v1/skill/save", consumes = "multipart/form-data")
    public ResponseWrapper<Skill> save(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile,
            @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile,
            @RequestParam(value = "gitUrl", required = false) String gitUrl) throws Exception {

        String iconUrl = null;
        if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
            iconUrl = uploadService
                    .setFolder("skills/icons/")
                    .uploadFile(iconFile);
        }

        String attachmentUrl = null;
        if (attachmentFile != null && !attachmentFile.isEmpty() && attachmentFile.getSize() > 0) {
            attachmentUrl = uploadService
                    .setFolder("skills/attachments/")
                    .uploadFile(attachmentFile);
        }

        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(name);
        skill.setDescription(description);
        skill.setContent(content);
        skill.setCategory(category);
        skill.setTags(tags);
        if (status != null) skill.setStatus(status);
        if (iconUrl != null) skill.setIconUrl(iconUrl);
        if (attachmentUrl != null) skill.setAttachmentUrl(attachmentUrl);
        if (gitUrl != null) skill.setGitUrl(gitUrl);
        if (userService.getCurrentUser() != null) {
            skill.setAuthorId(userService.getCurrentUser().getId());
        }

        return new ResponseWrapper<Skill>().success(skillService.save(skill));
    }

    @Operation(summary = "删除Skill")
    @PostMapping("/api/v1/skill/delete")
    public ResponseWrapper<?> delete(@RequestParam Long id) {
        skillService.delete(id);
        return new ResponseWrapper<>().success();
    }

    @Operation(summary = "记录下载")
    @PostMapping("/api/v1/skill/download")
    public ResponseWrapper<?> download(@RequestParam Long id) {
        skillService.increaseDownload(id);
        Skill skill = skillService.getById(id);
        return new ResponseWrapper<String>().success(MinioUrlUtil.toProxyUrl(skill.getAttachmentUrl()),"");
    }
}
