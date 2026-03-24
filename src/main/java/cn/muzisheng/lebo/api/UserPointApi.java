package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.service.UserPointService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 用户积分接口接口
 * 提供用户积分的查询、更新等功能
 */
@RestController
@RequestMapping("/point")
public class UserPointApi {
    private final UserPointService userPointService;
    public UserPointApi(UserPointService userPointService) {
        this.userPointService = userPointService;
    }
}
