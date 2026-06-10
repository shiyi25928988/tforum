package cc.shiyi.coleditor.forum.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String account;
    private String password;
}
