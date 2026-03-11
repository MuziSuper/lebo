package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.constant.Constant;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.exception.WXException;
import cn.muzisheng.lebo.param.WXCodeSession;
import cn.muzisheng.lebo.service.WXService;
import cn.muzisheng.lebo.utils.HttpClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序API工具类
 * 专注于微信小程序API调用，不包含JWT Token处理
 */

@Log4j2
@Component
public class WXServiceImpl implements WXService {


    private final ObjectMapper objectMapper = new ObjectMapper();
    @Getter
    private final String appId;
    @Getter
    private volatile String accessToken;
    @Getter
    private final String appSecret;


    public WXServiceImpl(Environment environment) {
        this.appId = environment.getProperty("APP_ID", String.class);
        this.appSecret = environment.getProperty("APP_SECRET", String.class);
        log.info("appId: {}, appSecret: {}", appId, appSecret);
    }

    /**
     * 微信登录凭证校验,已完成并检视代码
     *
     * @param code 前端传入的登录凭证
     * @return WXCodeSession 对象
     */
    public WXCodeSession code2Session(String code) {
        // 1. 构造请求参数
        Map<String, String> params = new HashMap<>();
        // 固定值，表示使用“授权码模式”换取 session
        params.put("grant_type", "authorization_code");
        // 小程序唯一标识
        params.put("appid", appId);
        // 小程序的 AppSecret
        params.put("secret", appSecret);
        // 小程序端拿到的 code
        params.put("js_code", code);

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
        String url = appendParams(Constant.WX_CODE2SESSION_URL, params);
        log.info("发送请求到微信接口: {}", url);
        String response = HttpClientUtil.get(url);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new WXException("解析微信登录凭证校验响应失败", e);
        }
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
        log.info("openid={},已获取微信登录凭证", result.get("openid"));
        return WXCodeSession.of(result);
    }

    /**
     * 获取微信小程序全局唯一后台接口调用凭据（access_token）- 稳定版接口
     * 已完成并检视代码
     *
     * @return access_token
     */
    public String getStableAccessToken() {
        // 构造请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "client_credential");
        params.put("appid", appId);
        params.put("secret", appSecret);
        params.put("force_refresh", false); // 默认使用普通模式，不会重复更新 token

        // 发送 POST 请求到微信稳定版接口
        String response = HttpClientUtil.postJson(Constant.WX_STABLE_TOKEN_URL, params);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 检查微信API返回的错误码
        if (result.containsKey("errcode")) {
            Integer errcode = (Integer) result.get("errcode");
            if (errcode != 0) {
                log.error("获取稳定版access_token失败: errcode={}, errmsg={}",
                        result.get("errcode"), result.get("errmsg"));
                throw new WXException("获取稳定版access_token失败: " + result.get("errmsg"));
            }
        }
        String accessToken = (String) result.get("access_token");
        log.info("获取稳定版access_token成功, 有效期: {}秒", result.get("expires_in"));
        return accessToken;
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

            String response = HttpClientUtil.get(Constant.WX_GET_USER_INFO_URL, params);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });

            // 检查微信API返回的错误码
            if (result.containsKey("errcode")) {
                Integer errcode = (Integer) result.get("errcode");
                if (errcode != 0) {
                    log.error("获取用户信息失败: errcode={}, errmsg={}",
                            result.get("errcode"), result.get("errmsg"));
                    throw new WXException("获取用户信息失败: " + result.get("errmsg"));
                }
            }

            log.info("获取用户信息成功: openid={}", openid);
            return result;
        } catch (Exception e) {
            log.error("调用获取用户信息API失败", e);
            throw new WXException("调用获取用户信息API失败", e);
        }
    }

    /**
     * 检验登录态
     * 校验服务器所保存的登录态 session_key 是否有效
     * signature = hmac_sha256(session_key, "")
     * 已完成并检视代码
     *
     * @return 是否校验成功
     */
    @Override
    public boolean checkSession(User user) {
        // 使用 session_key 对空字符串进行 HMAC-SHA256 签名
        String signature = hmacSha256(user.getSessionKey(), "");

        // 构造请求参数
        Map<String, String> params = new HashMap<>();
        params.put("openid", user.getOpenId());
        params.put("signature", signature);
        params.put("sig_method", "hmac_sha256");

        // 发送 GET 请求到微信检验登录态接口
        /*
         * checkSession 接口：
         * 介绍：校验服务器所保存的登录态 session_key 是否有效
         * 文档：<a href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-login/api_checksessionkey.html">...</a>
         * 入参：
         * - access_token：接口调用凭证（通过 URL 查询参数传递）
         * - openid：用户唯一标识符（通过请求体传递）
         * - signature：用户登录态签名，用 session_key 对空字符串签名得到的结果（通过请求体传递）
         * - sig_method：用户登录态签名的哈希方法，目前只支持 hmac_sha256（通过请求体传递）
         * 出参：
         * - errcode：错误码（0 表示成功）
         * - errmsg：错误信息
         */
        String url = appendParams(Constant.WX_CHECK_SESSION_URL, "access_token",
                getAccessToken());
        String response = HttpClientUtil.get(url, params);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 检查微信API返回的错误码
        if (result.containsKey("errcode")) {
            Integer errcode = (Integer) result.get("errcode");
            if (errcode == 0) {
                log.info("检验登录态成功: openid={}", user.getOpenId());
                return true;
            } else if (errcode == 41001 || errcode == 40014 || errcode == 40001 || errcode == 42007 || errcode == 42001 || errcode == 87009) {
                refreshAccessToken();
                throw new WXException("access_token 已过期，已刷新请再次请求");
            } else {
                log.warn("检验登录态失败: errcode={}, errmsg={}, openid={}",
                        result.get("errcode"), result.get("errmsg"), user.getOpenId());
                throw new WXException("检验登录态失败: " + result.get("errmsg"));
            }
        }
        log.warn("检验登录态失败: 未返回 errcode, openid={}", user.getOpenId());
        throw new WXException("检验登录态失败: 未返回 errcode");
    }

    /**
     * 重置登录态
     * 重置服务器所保存的登录态 session_key
     * signature = hmac_sha256(session_key, "")
     * 已完成并检视代码
     *
     * @return wxCodeSession 不包含union_id
     */
    @Override
    public WXCodeSession resetSession(User user) {
        // 使用 session_key 对空字符串进行 HMAC-SHA256 签名
        String signature = hmacSha256(user.getSessionKey(), "");

        // 构造请求参数
        Map<String, String> params = new HashMap<>();
        params.put("openid", user.getOpenId());
        params.put("signature", signature);
        params.put("sig_method", "hmac_sha256");

        // 发送 GET 请求到微信检验登录态接口
        /*
         * checkSession 接口：
         * 介绍：校验服务器所保存的登录态 session_key 是否有效
         * 文档：<a href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-login/api_checksessionkey.html">...</a>
         * 入参：
         * - access_token：接口调用凭证（通过 URL 查询参数传递）
         * - openid：用户唯一标识符（通过请求体传递）
         * - signature：用户登录态签名，用 session_key 对空字符串签名得到的结果（通过请求体传递）
         * - sig_method：用户登录态签名的哈希方法，目前只支持 hmac_sha256（通过请求体传递）
         * 出参：
         * - errcode：错误码（0 表示成功）
         * - errmsg：错误信息
         */
        String url = appendParams(Constant.WX_CHECK_SESSION_URL, "access_token",
                getAccessToken());
        String response = HttpClientUtil.get(url, params);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new WXException("解析微信登录凭证校验响应失败", e);
        }

        // 检查微信API返回的错误码
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
        log.info("openid={},已获取微信登录凭证", result.get("openid"));
        return WXCodeSession.of(result);
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
            accessToken = getStableAccessToken();
            log.info("微信 access_token 刷新成功");
        } catch (Exception e) {
            log.error("刷新微信 access_token 失败", e);
            // 刷新失败时不更新 token，继续使用旧的 token
        }
    }


    /**
     * HMAC-SHA256 签名计算
     *
     * @param data 待签名的数据
     * @param key  签名密钥
     * @return 十六进制格式的签名结果
     */
    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(signData);
        } catch (Exception e) {
            log.error("HMAC-SHA256 签名计算失败", e);
            throw new WXException("HMAC-SHA256 签名计算失败", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    /**
     * 向URL追加单个查询参数
     *
     * @param url         原始URL
     * @param paramsName  参数名称
     * @param paramsValue 参数值
     * @return 拼接后的完整URL（格式：url?paramsName=paramsValue）
     */
    private String appendParams(String url, String paramsName, String paramsValue) {
        // 使用StringBuffer进行字符串拼接，适用于多线程环境
        StringBuffer sb = new StringBuffer(url);
        // 添加参数分隔符
        sb.append("?");
        // 添加参数名
        sb.append(paramsName);
        // 添加键值对连接符
        sb.append("=");
        // 添加参数值
        sb.append(paramsValue);
        // 返回拼接后的完整URL
        return sb.toString();
    }

    /**
     * 将参数追加到URL后面，形成带查询参数的完整URL
     *
     * @param url    原始URL地址
     * @param params 要追加的参数映射表，key为参数名，value为参数值
     * @return 拼接后的完整URL字符串
     */
    private String appendParams(String url, Map<String, String> params) {
        // 使用StringBuffer进行字符串拼接，效率优于String直接拼接
        StringBuffer sb = new StringBuffer(url);
        if(params!= null&& !params.isEmpty()) {
            sb.append("?");
            // 遍历参数映射表中的所有键值对
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // 添加参数分隔符"?"
                // 添加参数名
                sb.append(entry.getKey());
                // 添加键值分隔符"="
                sb.append("=");
                // 添加参数值
                sb.append(entry.getValue());
                // 添加参数分隔符"&"
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        // 返回拼接后的完整URL
        return sb.toString();
    }
}