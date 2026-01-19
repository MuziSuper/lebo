package cn.muzisheng.lebo.utils;

import cn.muzisheng.lebo.config.WechatMiniProgramConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序API工具类
 * 专注于微信小程序API调用，不包含JWT Token处理
 */
@Slf4j
@Component
public class WxUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private WechatMiniProgramConfig config;
    
    // 微信API常量
    private static final String WX_API_BASE = "https://api.weixin.qq.com";
    private static final String CODE2SESSION_URL = WX_API_BASE + "/sns/jscode2session";
    private static final String ACCESS_TOKEN_URL = WX_API_BASE + "/cgi-bin/token";
    private static final String GET_USER_INFO_URL = WX_API_BASE + "/cgi-bin/user/info";
    
    /**
     * 微信登录凭证校验，通过code换取session_key和openid
     * 
     * @param code 小程序端登录时获取的code
     * @return 包含session_key和openid的响应
     */
    public Map<String, Object> code2Session(String code) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("appid", config.getAppId());
            params.put("secret", config.getAppSecret());
            params.put("js_code", code);
            params.put("grant_type", "authorization_code");
            
            String response = HttpClientUtils.get(CODE2SESSION_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            
            // 检查微信API返回的错误码
            if (result.containsKey("errcode")) {
                Integer errcode = (Integer) result.get("errcode");
                if (errcode != 0) {
                    log.error("微信登录凭证校验失败: errcode={}, errmsg={}", 
                            result.get("errcode"), result.get("errmsg"));
                    throw new RuntimeException("微信登录凭证校验失败: " + result.get("errmsg"));
                }
            }
            
            log.info("微信登录凭证校验成功: openid={}", result.get("openid"));
            return result;
        } catch (Exception e) {
            log.error("调用微信登录凭证校验API失败", e);
            throw new RuntimeException("调用微信登录凭证校验API失败", e);
        }
    }
    
    /**
     * 获取微信小程序全局唯一后台接口调用凭据（access_token）
     * 
     * @return 包含access_token的响应
     */
    public Map<String, Object> getAccessToken() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "client_credential");
            params.put("appid", config.getAppId());
            params.put("secret", config.getAppSecret());
            
            String response = HttpClientUtils.get(ACCESS_TOKEN_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            
            // 检查微信API返回的错误码
            if (result.containsKey("errcode")) {
                Integer errcode = (Integer) result.get("errcode");
                if (errcode != 0) {
                    log.error("获取access_token失败: errcode={}, errmsg={}", 
                            result.get("errcode"), result.get("errmsg"));
                    throw new RuntimeException("获取access_token失败: " + result.get("errmsg"));
                }
            }
            
            log.info("获取access_token成功");
            return result;
        } catch (Exception e) {
            log.error("调用获取access_token API失败", e);
            throw new RuntimeException("调用获取access_token API失败", e);
        }
    }
    
    /**
     * 获取用户信息（需要access_token和openid）
     * 
     * @param accessToken 接口调用凭据
     * @param openid      用户的唯一标识
     * @param lang        返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return 用户信息
     */
    public Map<String, Object> getUserInfo(String accessToken, String openid, String lang) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("openid", openid);
            params.put("lang", lang != null ? lang : "zh_CN");
            
            String response = HttpClientUtils.get(GET_USER_INFO_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            
            // 检查微信API返回的错误码
            if (result.containsKey("errcode")) {
                Integer errcode = (Integer) result.get("errcode");
                if (errcode != 0) {
                    log.error("获取用户信息失败: errcode={}, errmsg={}", 
                            result.get("errcode"), result.get("errmsg"));
                    throw new RuntimeException("获取用户信息失败: " + result.get("errmsg"));
                }
            }
            
            log.info("获取用户信息成功: openid={}", openid);
            return result;
        } catch (Exception e) {
            log.error("调用获取用户信息API失败", e);
            throw new RuntimeException("调用获取用户信息API失败", e);
        }
    }
    
    /**
     * 检查微信API响应是否成功
     * 
     * @param response 微信API响应
     * @return 是否成功
     */
    private boolean isApiResponseSuccess(Map<String, Object> response) {
        if (response == null || !response.containsKey("errcode")) {
            return true; // 没有errcode字段认为成功
        }
        
        Integer errcode = (Integer) response.get("errcode");
        return errcode != null && errcode == 0;
    }
    
    /**
     * 从响应中获取错误信息
     * 
     * @param response 微信API响应
     * @return 错误信息
     */
    private String getApiErrorMessage(Map<String, Object> response) {
        if (response == null) {
            return "未知错误";
        }
        
        Integer errcode = (Integer) response.get("errcode");
        String errmsg = (String) response.get("errmsg");
        
        return String.format("错误码: %d, 错误信息: %s", errcode, errmsg);
    }
}
