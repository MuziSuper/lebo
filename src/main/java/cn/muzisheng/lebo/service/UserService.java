package cn.muzisheng.lebo.service;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 微信小程序登录
     *
     * @param code 小程序端传来的临时登录凭证
     * @return 用户的openid
     */
    String login(String code);



}
