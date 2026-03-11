package cn.muzisheng.lebo.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 配置存储
 **/
@Getter
@Configuration
public class StorageConfig {
    @Value("${storage.path}")
    private String location;

}