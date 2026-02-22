package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户相关API
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private UserService userService;


    /**
     * 微信小程序登录接口
     *
     * @param jscode 微信小程序登录code
     * @return 用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<Result<User>> login(@RequestParam(name = "jscode") String jscode) {
        return userService.login(jscode);
    }


    /**
     * 微信小程序注销接口
     *
     * @return token和用户信息
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Boolean>> unlogin() {
        return userService.logout();
    }

//    @GetMapping("/info")
//    public Map<String, Object> getCurrentUserInfo() {
//
//    }

}
