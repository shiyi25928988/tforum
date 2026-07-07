package cc.shiyi.coleditor.forum.controller.user;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cc.shiyi.coleditor.forum.request.LoginRequest;
import cc.shiyi.coleditor.forum.request.PasswordChangeRequest;
import cc.shiyi.coleditor.user.dto.UserInfo;
import cc.shiyi.coleditor.user.dto.UserRegisterInfo;
import cc.shiyi.coleditor.user.service.AvatarService;
import cc.shiyi.coleditor.user.service.UserService;
import cc.shiyi.coleditor.user.table.Avatar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "用户管理")
public class UserController {

    UserService userService;
    AvatarService avatarService;

    @Operation(summary = "用户注册")
    @PostMapping("/api/v1/user/register")
    public ResponseWrapper<Void> registerUser(@RequestBody UserRegisterInfo userRegisterInfo) throws Exception {
        String decodedPassword = new String(Base64.getDecoder().decode(userRegisterInfo.getPassword()));
        userRegisterInfo.setPassword(decodedPassword);
        userService.registerUser(userRegisterInfo);
        return new ResponseWrapper().success("注册成功！");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/api/v1/user/login")
    public ResponseWrapper<UserInfo> login(@RequestBody LoginRequest request) throws Exception {
        String password = new String(Base64.getDecoder().decode(request.getPassword()));
        return new ResponseWrapper().success(userService.login(request.getAccount(), password));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/api/v1/user/logout")
    public ResponseWrapper<Void> logout() throws Exception {
        userService.logout();
        return new ResponseWrapper().success();
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/api/v1/user/update")
    public ResponseWrapper<Void> updateUserInfo(@RequestBody UserInfo userInfo) throws Exception {
        userService.updateUserInfo(userInfo);
        return new ResponseWrapper().success();
    }

    @Operation(summary = "更新用户密码")
    @PostMapping("/api/v1/user/updatePassword")
    public ResponseWrapper<Void> updatePassword(@RequestBody PasswordChangeRequest request) throws Exception {
        String oldPassword = new String(Base64.getDecoder().decode(request.getOldPassword()));
        String newPassword = new String(Base64.getDecoder().decode(request.getNewPassword()));
        userService.updatePassword(oldPassword, newPassword);
        return new ResponseWrapper().success();
    }

    @Operation(summary = "获取随机头像")
    @GetMapping("/api/v1/user/getRandomAvatar")
    public ResponseWrapper<Avatar> getRandomAvatar() throws Exception {
        return new ResponseWrapper().success(avatarService.getRandomAvatar());
    }

    @Operation(summary = "获取所有头像")
    @GetMapping("/api/v1/user/getAllAvatar")
    public ResponseWrapper<List<Avatar>> getAllAvatar() throws Exception {
        return new ResponseWrapper().success(avatarService.getAllAvatar());
    }

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/api/v1/user/{id}")
    public ResponseWrapper<UserInfo> getUserById(@PathVariable("id") Long id) {
        return new ResponseWrapper<UserInfo>().success(userService.getUserInfoById(id));
    }

    @Operation(summary = "获取当前登录用户信息（含 token）")
    @GetMapping("/api/v1/user/current")
    public ResponseWrapper<UserInfo> getCurrentInfo() {
        return new ResponseWrapper<UserInfo>().success(userService.getCurrentUserInfo());
    }

    @Operation(summary = "第三方应用验证 token，返回用户信息")
    @GetMapping("/api/v1/user/verifyToken")
    public ResponseWrapper<UserInfo> verifyToken(@RequestParam String token) {
        UserInfo info = userService.verifyToken(token);
        if (info == null) {
            return new ResponseWrapper<UserInfo>().fail("token 无效");
        }
        return new ResponseWrapper<UserInfo>().success(info);
    }
}
