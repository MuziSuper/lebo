package cn.muzisheng.lebo.param;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 */
@AllArgsConstructor
@Data
public class LoginResponseParam {
    /**
     * JWT token
     */
    private String token;

    /**
     * 用户openid
     */
    private String openid;
}
