package cn.muzisheng.lebo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 微信云托管对象存储配置类
 * 
 * <p>配置微信云托管对象存储相关的参数：
 * <ul>
 *   <li>云环境ID（env）- 必填</li>
 *   <li>默认下载链接有效期 - 可选，默认24小时</li>
 * </ul>
 * 
 * <p>API地址已固化在 {@link cn.muzisheng.lebo.constant.Constant} 类中
 * 
 * <p>配置项示例（application.properties）：
 * <pre>
 * wx.cloud.env=your-env-id
 * wx.cloud.download.default-max-age=86400
 * </pre>
 */
@Getter
@Configuration
public class WxCloudStorageConfig {

    /**
     * 微信云托管环境ID
     * 在微信云托管控制台可查看
     */
    @Value("${wx.cloud.env:}")
    private String env;

    /**
     * 下载链接默认有效期（秒）
     * 默认86400秒（24小时）
     */
    @Value("${wx.cloud.download.default-max-age:86400}")
    private Long defaultMaxAge;
}
