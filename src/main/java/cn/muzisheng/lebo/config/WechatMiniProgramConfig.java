package cn.muzisheng.lebo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置类
 */
@Data
@Configuration
public class WechatMiniProgramConfig {
    
    @Value("${wechat.mini-program.app-id}")
    private String appId;
    
    @Value("${wechat.mini-program.app-secret}")
    private String appSecret;
    
    @Value("${wechat.mini-program.api.timeout}")
    private int apiTimeout;
 }
