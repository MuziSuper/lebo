package cn.muzisheng.lebo.dto;

import lombok.Data;
/**
 * 微信小程序登录参数
 */
@Data
public class LoginDTO {
    private String nickName;
    private String avatar;
    private Integer gender;
}
