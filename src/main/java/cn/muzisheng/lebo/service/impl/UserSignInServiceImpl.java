package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.UserSignIn;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.mapper.UserSignInMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.service.UserSignInService;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 用户签到服务实现类
 * 实现用户签到的核心业务逻辑，包括签到状态管理和积分发放
 */
@Log4j2
@Service
public class UserSignInServiceImpl extends ServiceImpl<UserSignInMapper, UserSignIn> implements UserSignInService {

    /** 基础签到积分 */
    private static final Long SIGN_BASE_POINT = 10L;

    /** 连续签到奖励积分 */
    private static final Long CONTINUOUS_BONUS_POINT = 10L;

    /** 触发奖励的连续签到天数阈值 */
    private static final int CONTINUOUS_DAYS_THRESHOLD = 3;

    private final UserPointService userPointService;

    public UserSignInServiceImpl(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    /**
     * 每日签到获取积分
     * <p>
     * 签到流程：
     * 1. 获取当前用户信息
     * 2. 检查今日是否已签到
     * 3. 计算连续签到天数（断签则重置为1）
     * 4. 更新签到记录
     * 5. 发放积分（基础积分 + 连续签到奖励）
     * </p>
     *
     * @return 签到结果（true表示签到成功）
     * @throws UserPointException 当签到失败时抛出
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = UserPointException.class)
    public ResponseEntity<Result<Boolean>> sign() throws UserPointException {
        Response<Boolean> response = new Response<>();

        // 从线程上下文获取当前用户openid
        String openId = UserThreadUtil.getCurrentOpenId();
        if (openId == null) {
            log.error("签到失败：未获取到当前用户信息");
            throw new UserPointException("未获取到当前用户信息");
        }

        LocalDate today = LocalDate.now();

        // 查询用户签到记录
        QueryWrapper<UserSignIn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openId);
        UserSignIn userSignIn = this.getOne(queryWrapper);

        if (userSignIn == null) {
            // 首次签到：创建签到记录并发放基础积分
            userSignIn = new UserSignIn();
            userSignIn.setOpenId(openId);
            userSignIn.setLastSignDate(today);
            userSignIn.setContinuousDays(1);
            this.save(userSignIn);

            userPointService.updatePoint(openId, SIGN_BASE_POINT);
            log.info("openid={}, 首次签到成功，获得{}积分", openId, SIGN_BASE_POINT);
        } else {
            // 检查今日是否已签到
            if (userSignIn.getLastSignDate() != null && userSignIn.getLastSignDate().equals(today)) {
                log.warn("openid={}, 今日已签到", openId);
                throw new UserPointException("今日已签到，请勿重复签到");
            }

            // 计算连续签到天数：如果上次签到是昨天，则连续天数+1；否则重置为1
            int continuousDays = 1;
            if (userSignIn.getLastSignDate() != null && userSignIn.getLastSignDate().equals(today.minusDays(1))) {
                continuousDays = userSignIn.getContinuousDays() + 1;
            }

            // 更新签到记录
            userSignIn.setLastSignDate(today);
            userSignIn.setContinuousDays(continuousDays);
            this.updateById(userSignIn);

            // 计算积分：基础积分 + 连续签到奖励
            Long totalPoint = SIGN_BASE_POINT;
            if (continuousDays >= CONTINUOUS_DAYS_THRESHOLD) {
                totalPoint += CONTINUOUS_BONUS_POINT;
                log.info("openid={}, 连续签到{}天，额外奖励{}积分", openId, continuousDays, CONTINUOUS_BONUS_POINT);
            }

            // 发放积分
            userPointService.updatePoint(openId, totalPoint);
            log.info("openid={}, 签到成功，获得{}积分", openId, totalPoint);
        }

        response.setData(true);
        return response.value();
    }
}
