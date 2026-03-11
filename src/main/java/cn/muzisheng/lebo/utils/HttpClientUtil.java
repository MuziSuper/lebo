package cn.muzisheng.lebo.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP客户端工具类 - 基于Apache HttpClient 4.5.14
 * 专门用于调用微信服务器API
 */

@Log4j2
public class HttpClientUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static PoolingHttpClientConnectionManager connectionManager;
    private static CloseableHttpClient httpClient;

    static {
        // 初始化连接池
        initializeConnectionPool();
    }

    /**
     * 初始化HTTP客户端连接池
     */
    private static void initializeConnectionPool() {
        // 创建连接池管理器
        connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(200);
        // 设置每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(20);
        // 设置连接存活时间
        connectionManager.setDefaultSocketConfig(
            org.apache.http.config.SocketConfig.custom()
                .setSoTimeout(30000)
                .build()
        );

        // 配置请求参数
        // 从连接池获取连接超时
        // 响应超时
        // 连接超时
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10000) // 从连接池获取连接超时
                .setSocketTimeout(30000) // 响应超时
                .setConnectTimeout(15000) // 连接超时
                .build();

        // 创建HTTP客户端
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        // 注册JVM关闭钩子，优雅关闭连接池
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (connectionManager != null) {
                    connectionManager.close();
                }
            } catch (IOException e) {
                log.error("关闭HTTP客户端连接池失败", e);
            }
        }));
    }

    /**
     * 执行GET请求
     *
     * @param url 请求URL
     * @return 响应内容
     */
    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 执行带参数的GET请求
     *
     * @param url    请求URL
     * @param params 请求参数
     * @return 响应内容
     */
    public static String get(String url, Map<String, String> params) {
        try {
            // 构造带参数的URL
            if (params != null && !params.isEmpty()) {
                URIBuilder uriBuilder = new URIBuilder(url);
                params.forEach(uriBuilder::addParameter);
                url = uriBuilder.build().toString();
            }

            HttpGet httpGet = new HttpGet(url);
            return execute(httpGet);
        } catch (URISyntaxException e) {
            log.error("构建URL失败: {}", url, e);
            throw new RuntimeException("构建URL失败", e);
        }
    }

    /**
     * 执行POST请求（JSON格式）
     *
     * @param url  请求URL
     * @param data 请求数据对象
     * @return 响应内容
     */
    public static String postJson(String url, Object data) {
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));
            return execute(httpPost);
        } catch (Exception e) {
            log.error("发送JSON POST请求失败: {}", url, e);
            throw new RuntimeException("发送POST请求失败", e);
        }
    }

    /**
     * 执行POST请求（表单格式）
     *
     * @param url    请求URL
     * @param params 表单参数
     * @return 响应内容
     */
    public static String postForm(String url, Map<String, String> params) {
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            if (params != null) {
                params.forEach((key, value) ->
                        nameValuePairs.add(new BasicNameValuePair(key, value))
                );
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
            return execute(httpPost);
        } catch (Exception e) {
            log.error("发送表单POST请求失败: {}", url, e);
            throw new RuntimeException("发送POST请求失败", e);
        }
    }

    /**
     * 执行DELETE请求
     *
     * @param url 请求URL
     * @return 响应内容
     */
    public static String delete(String url) {
        HttpDelete httpDelete = new HttpDelete(url);
        return execute(httpDelete);
    }

    /**
     * 执行PUT请求（JSON格式）
     *
     * @param url  请求URL
     * @param data 请求数据对象
     * @return 响应内容
     */
    public static String putJson(String url, Object data) {
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPut.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));
            return execute(httpPut);
        } catch (Exception e) {
            log.error("发送JSON PUT请求失败: {}", url, e);
            throw new RuntimeException("发送PUT请求失败", e);
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param request HTTP请求对象
     * @return 响应内容
     */
    private static String execute(HttpRequestBase request) {
        try {
            log.debug("执行HTTP请求: {} {}", request.getMethod(), request.getURI());

            try (org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                // 确保实体内容被完全消费
                EntityUtils.consume(entity);

                log.debug("HTTP请求响应: {} {}", response.getStatusLine().getStatusCode(), result);
                return result;
            }
        } catch (IOException e) {
            log.error("HTTP请求执行失败: {} {}", request.getMethod(), request.getURI(), e);
            throw new RuntimeException("HTTP请求执行失败", e);
        }
    }

    /**
     * 执行HTTP请求并返回指定类型的对象
     *
     * @param request HTTP请求对象
     * @param type    返回对象类型
     * @param <T>     泛型类型
     * @return 响应内容转换的对象
     */
    public static <T> T executeAndParse(HttpRequestBase request, TypeReference<T> type) {
        String responseBody = execute(request);
        try {
            return objectMapper.readValue(responseBody, type);
        } catch (Exception e) {
            log.error("JSON解析失败: {}", responseBody, e);
            throw new RuntimeException("JSON解析失败", e);
        }
    }

    /**
     * 获取连接池状态信息
     *
     * @return 连接池状态信息
     */
    public static String getConnectionPoolStats() {
        return connectionManager.getTotalStats().toString();
    }
}