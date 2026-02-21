package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.model.Result;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<Result<User>> login(String code);
    ResponseEntity<Result<Boolean>> logout();
    void updateLastLogin(String openid);

    User getUserByOpenId(String openid);
}
