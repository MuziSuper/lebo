package cn.muzisheng.lebo.utils;

import cn.muzisheng.lebo.service.WXService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Timed;

import java.sql.Time;

/**
 * 定时票据工具类
 * 单例模式，用于定时刷新微信 access_token
 * 每小时自动刷新一次
 */
@Slf4j
@Component
public class TimedVouchersUtil {

    @Getter
    private volatile String accessToken;

    private final WXService wxService;


    /**
     * 私有构造函数，防止外部实例化
     */
    private TimedVouchersUtil(WXService wxService) {
        this.wxService = wxService;
    }

    /**
     * 初始化方法，启动时立即刷新一次
     */
    @PostConstruct
    public void init() {
        log.info("TimedVouchersUtil 初始化，开始首次刷新微信 access_token");
        refreshAccessToken();
    }

    /**
     * 定时刷新 access_token
     * 每小时执行一次
     * cron 表达式: 0 0 * * * ? (每小时的0分0秒执行)
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduledRefresh() {
        log.info("定时任务触发，开始刷新微信 access_token");
        refreshAccessToken();
    }

    /**
     * 刷新 access_token
     */
    public void refreshAccessToken() {
        try {
            accessToken = wxService.getStableAccessToken();
            log.info("微信 access_token 刷新成功");
        } catch (Exception e) {
            log.error("刷新微信 access_token 失败", e);
            // 刷新失败时不更新 token，继续使用旧的 token
        }
    }

}
