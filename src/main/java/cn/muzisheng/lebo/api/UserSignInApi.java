package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.UserSignInService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户签到API接口
 * 提供用户签到相关的REST接口
 */
@RestController
@RequestMapping("/signin")
public class UserSignInApi {

    private final UserSignInService userSignInService;

    public UserSignInApi(UserSignInService userSignInService) {
        this.userSignInService = userSignInService;
    }

    /**
     * 每日签到接口
     * <p>
     * 签到规则：
     * - 基础签到获得10积分
     * - 连续签到3天及以上额外获得10积分
     * - 断签后连续天数重置为1
     * </p>
     *
     * @return 签到结果
     */
    @PostMapping
    public ResponseEntity<Result<Boolean>> sign() {
        return userSignInService.sign();
    }
}
