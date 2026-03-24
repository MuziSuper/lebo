package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.UserSignIn;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.model.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

/**
 * 用户签到服务接口
 * 提供用户签到相关的业务操作
 */
public interface UserSignInService extends IService<UserSignIn> {
    /**
     * 每日签到获取积分
     * 签到规则：
     * - 基础签到获得10积分
     * - 连续签到3天及以上额外获得10积分
     * - 断签后连续天数重置为1
     *
     * @return 签到结果（true表示签到成功）
     * @throws UserPointException 当签到失败时抛出（如重复签到、用户信息获取失败等）
     */
    ResponseEntity<Result<Boolean>> sign() throws UserPointException;
}
