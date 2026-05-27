package cn.muzisheng.lebo.config;

import cn.muzisheng.lebo.interceptor.JwtAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 用于注册拦截器和配置白名单路径
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/error",
                        "/user/login",
                        "/user/register",
                        "/files/**",
                        "/upload",
                        "/user/bossLogin",
                        "/slideshow/fileIds",
                        "/awards/list"
                );  // 白名单路径
    }
     @Bean
     public CorsFilter corsFilter() {
         CorsConfiguration config = new CorsConfiguration();
         // 允许所有源（生产环境建议指定具体域名）
         config.addAllowedOriginPattern("*");
         // 允许所有方法
         config.addAllowedMethod("*");
         // 允许所有请求头
         config.addAllowedHeader("*");
         // 【关键】暴露 Authorization 头给前端，否则前端 JS 读不到
         config.addExposedHeader("Authorization");
         // 允许携带凭证（Cookie/Headers）
         config.setAllowCredentials(true);

         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         source.registerCorsConfiguration("/**", config);
         return new CorsFilter(source);
     }
}
