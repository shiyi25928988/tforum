package cc.shiyi.coleditor.user.converter;

import cc.shiyi.coleditor.user.dto.UserInfo;
import cc.shiyi.coleditor.user.dto.UserRegisterInfo;
import cc.shiyi.coleditor.user.table.User;
import cn.dev33.satoken.stp.StpUtil;

/**
 * User 实体与 DTO 转换工具
 */
public final class UserConverter {

    private UserConverter() {}

    public static UserInfo toUserInfo(User user) {
        if (user == null) {
            return null;
        }
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setAccount(user.getAccount());
        info.setPassword(null); // 不返回密码
        info.setEmail(user.getEmail());
        info.setPhone(user.getPhone());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole());
        info.setPermission(user.getPermission());
        info.setStatus(user.getStatus());
        info.setToken(StpUtil.getTokenValue());
        return info;
    }

    public static User toUser(UserRegisterInfo registerInfo) {
        if (registerInfo == null) {
            return null;
        }
        User user = new User();
        user.setUsername(registerInfo.getUsername());
        user.setAccount(registerInfo.getAccount());
        user.setPassword(registerInfo.getPassword());
        user.setEmail(registerInfo.getEmail());
        user.setPhone(registerInfo.getPhone());
        user.setAvatar(registerInfo.getAvatar());
        return user;
    }
}
