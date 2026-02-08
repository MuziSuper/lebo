package cn.muzisheng.lebo.param;

import lombok.Data;

@Data
public class LoginRequestParam {
    /**
     * 微信小程序临时登录凭证code
     */
    private String code;
}
