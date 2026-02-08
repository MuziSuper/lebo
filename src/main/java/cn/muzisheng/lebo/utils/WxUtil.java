package cn.muzisheng.lebo.utils;

import cn.muzisheng.lebo.config.WechatMiniProgramConfig;
import cn.muzisheng.lebo.exception.WXException;
import cn.muzisheng.lebo.param.WXCodeSession;
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
     * @param code 小程序端通过wx.login获取的临时登录凭证code后，传到开发者服务器调用此接口完成登录流程
     * @return 包含session_key和openid的响应
     */
    public WXCodeSession code2Session(String code) {
        try {
            // 1. 构造请求参数
            Map<String, String> params = new HashMap<>();
            // 小程序唯一标识
            params.put("appid", config.getAppId());
            // 小程序的 AppSecret
            params.put("secret", config.getAppSecret());
            // 小程序端拿到的 code
            params.put("js_code", code);
            // 固定值，表示使用“授权码模式”换取 session
            params.put("grant_type", "authorization_code");

            // 2. 发送 GET 请求到微信接口
            /*
             * code2Session接口：
             * 介绍：登录凭证校验接口，用于获取用户的 openid 和 session_key。
             * 文档：<a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html">...</a>
             * 入参：
             * - appid：小程序 appId
             * - secret：小程序 appSecret
             * - js_code：登录时获取的 code
             * - grant_type：固定值 authorization_code
             * 出参：
             * - openid：用户唯一标识
             * - session_key：会话密钥
             * - unionid：用户在开放平台的唯一标识符（若当前小程序已绑定到微信开放平台帐号下会返回）
             * - errcode：错误码
             * - errmsg：错误信息
             */
            String response = HttpClientUtil.get(CODE2SESSION_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            // 检查微信API返回的错误码
            // 4. 检查微信返回的错误码
            if (result.containsKey("errcode")) {
                Integer errcode = (Integer) result.get("errcode");
                if (errcode != 0) {
                    // 错误码非 0 时，打印日志并抛出异常
                    log.error("errcode={}, errmsg={}，微信登录凭证校验失败",
                            result.get("errcode"), result.get("errmsg"));
                    throw new WXException((String) result.get("errmsg"));
                }
            }

            // 5. 校验成功，打印 openid 并返回结果
            log.info("openid={},微信登录凭证校验失败", result.get("openid"));
            return WXCodeSession.of(result);
        } catch (Exception e) {
            // 6. 捕获网络、解析等所有异常，统一包装后抛出
            log.error("调用微信登录凭证校验API失败", e);
            throw new WXException("调用微信登录凭证校验API失败", e);
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

            String response = HttpClientUtil.get(ACCESS_TOKEN_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });

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

            String response = HttpClientUtil.get(GET_USER_INFO_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });

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
