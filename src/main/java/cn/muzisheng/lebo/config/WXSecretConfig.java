package cn.muzisheng.lebo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
@Configuration
public class WXSecretConfig {
    public String appId;
    public String appSecret;
    public WXSecretConfig(Environment env){
        appId=env.getProperty("APP_ID");
        appSecret=env.getProperty("APP_SECRET");
    }

}
