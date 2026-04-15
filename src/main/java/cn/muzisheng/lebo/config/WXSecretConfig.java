package cn.muzisheng.lebo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
@Data
@Configuration
public class WXSecretConfig {
    @Value("${APP_ID}")
    private String appId;

    @Value("${APP_SECRET}")
    private String appSecret;


}
