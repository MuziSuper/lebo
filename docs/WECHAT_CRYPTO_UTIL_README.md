# 微信开放平台API加解密工具类使用指南

## 概述

`WechatApiCryptoUtil` 是一个用于微信开放平台API加解密的工具类，支持：
- AES256_GCM 对称加密算法
- RSAwithSHA256 非对称签名算法
- 完整的安全验证机制（时间戳、appid等）
- 线程安全设计

## 文件结构

```
src/main/java/cn/muzisheng/lebo/
├── dto/
│   ├── WechatCryptoConfig.java          # 加密配置类
│   ├── WechatEncryptedRequest.java       # 加密请求数据类
│   └── WechatEncryptedResponse.java      # 加密响应数据类
├── exception/
│   └── WechatCryptoException.java       # 加密异常类
├── utils/
│   └── WechatApiCryptoUtil.java         # 核心工具类
└── api/
    └── WechatCryptoExampleApi.java      # 使用示例控制器
```

## 配置步骤

### 1. 在小程序管理后台配置密钥

1. 登录小程序管理后台
2. 进入「开发 - 开发管理 - 开发设置 - API安全」
3. 点击「开始配置」并扫码验证
4. 配置对称密钥（AES256_GCM）
   - 点击「随机生成密钥」
   - 点击「下载密钥」并保存到本地
   - 记录密钥编号（Sn）
5. 配置应用私钥（RSAwithSHA256）
   - 点击「随机生成密钥对」
   - 点击「下载私钥」并保存到本地
   - 填充应用公钥到输入框
   - 点击「确认」
   - 记录密钥编号（Sn）
6. 下载平台证书
   - 点击「下载证书」
   - 保存证书文件到本地
   - 记录证书编号

### 2. 添加Maven依赖

注意：如果只使用AES256_GCM和RSAwithSHA256算法，不需要添加额外的依赖，Java标准库已经支持。

## 使用方法

### 1. 加密请求

```java


// 1. 创建配置对象
WechatCryptoConfig config = WechatCryptoConfig.builder()
        .appId("your_appid")                                    // 小程序AppID
        .symmetricKey("otUpngOjU+nVQaWJIC3D/yMLV17RKaP6t4Ot9tbnzLY=")  // 对称密钥（base64）
        .symmetricKeySn("fa05fe1e5bcc79b81ad5ad4b58acf787")     // 对称密钥编号
        .privateKey("MIIEowIBAAKCAQEA3FoQOmOl5/CF5hF7ta4...")   // 私钥（base64）
        .asymmetricKeySn("97845f6ed842ea860df6fdf65941ff56")     // 非对称密钥编号
        .urlPath("https://api.weixin.qq.com/wxa/getuserriskrank")  // API路径
        .algorithm("AES256_GCM")                                // 加密算法
        .signatureAlgorithm("RSAwithSHA256")                     // 签名算法
        .build();

        // 2. 准备原始数据
        Map<String, Object> originalData = new HashMap<>();
originalData.

        put("openid","oEWzBfmdLqhFS2mTXCo2E4Y9gJAM");
originalData.

        put("scene",0);
originalData.

        put("client_ip","127.0.0.1");

        // 3. 加密请求数据
        WechatEncryptedRequest encryptedRequest = WechatApiCryptoUtil.encryptRequest(originalData, config);

        // 4. 构建HTTP请求头
        Map<String, String> headers = new HashMap<>();
headers.

        put("Wechatmp-Appid",encryptedRequest.getAppId());
        headers.

        put("Wechatmp-TimeStamp",String.valueOf(encryptedRequest.getTimestamp()));
        headers.

        put("Wechatmp-Signature",encryptedRequest.getSignature());
        headers.

        put("Wechatmp-Serial",encryptedRequest.getKeySn());
        headers.

        put("Content-Type","application/json");

        // 5. 发送POST请求到微信API
        String requestBody = objectMapper.writeValueAsString(encryptedRequest.getEncryptedData());
        String response = httpClient.post(url, requestBody, headers);
```

### 2. 解密响应

```java


// 1. 解析HTTP响应头
String respAppid = responseHeader.get("Wechatmp-Appid");
        Long respTimestamp = Long.parseLong(responseHeader.get("Wechatmp-TimeStamp"));
        String respSignature = responseHeader.get("Wechatmp-Signature");
        String respSn = responseHeader.get("Wechatmp-Serial");

        // 2. 解析响应体（包含加密数据）
        Map<String, String> encryptedData = objectMapper.readValue(responseBody, Map.class);

        // 3. 构建加密响应对象
        WechatEncryptedResponse encryptedResponse = WechatEncryptedResponse.builder()
                .encryptedData(encryptedData)
                .signature(respSignature)
                .timestamp(respTimestamp)
                .certificateSn(respSn)
                .build();

        // 4. 加载平台证书
        String platformCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIID0jCCArqgAwIBAgIUeE+Yy7vM/o+eHHsfM+1bGJJEZTQwDQYJKoZIhvcNAQEL\n" +
                "... (证书内容) ...\n" +
                "-----END CERTIFICATE-----";

        // 5. 解密响应数据
        Map<String, Object> decryptedData = WechatApiCryptoUtil.decryptResponse(
                encryptedResponse,
                platformCertificate,
                respSn,
                config
        );

        // 6. 使用解密后的数据
        Object errcode = decryptedData.get("errcode");
        Object errmsg = decryptedData.get("errmsg");
        Object riskRank = decryptedData.get("risk_rank");
```

### 3. 通过API接口使用

工具类还提供了RESTful API接口，可以通过HTTP请求进行测试：

#### 加密请求

```http
POST /api/wechat/crypto/encrypt
Content-Type: application/json

{
  "originalData": {
    "openid": "oEWzBfmdLqhFS2mTXCo2E4Y9gJAM",
    "scene": 0,
    "client_ip": "127.0.0.1"
  },
  "config": {
    "appId": "wxba6223c06417af7b",
    "symmetricKey": "otUpngOjU+nVQaWJIC3D/yMLV17RKaP6t4Ot9tbnzLY=",
    "symmetricKeySn": "fa05fe1e5bcc79b81ad5ad4b58acf787",
    "privateKey": "MIIEowIBAAKCAQEA3FoQOmOl5/... (base64编码的私钥)",
    "asymmetricKeySn": "97845f6ed842ea860df6fdf65941ff56",
    "urlPath": "https://api.weixin.qq.com/wxa/getuserriskrank",
    "algorithm": "AES256_GCM",
    "signatureAlgorithm": "RSAwithSHA256"
  }
}
```

#### 解密响应

```http
POST /api/wechat/crypto/decrypt
Content-Type: application/json

{
  "encryptedData": {
    "iv": "r2WDQt56rEAmMuoR",
    "data": "HExs66Ik3el+iM4IpeQ7SMEN934FRLFYOd3EmeaIrp...",
    "authtag": "z2BFD8QctKXTuBlhICGOjQ=="
  },
  "signature": "Ht0VfQkkEweJ4hU266C14Aj64H9AXfkwNi5zxUZETCvR...",
  "timestamp": 1635927956,
  "certificateSn": "79ba700ea147819f640941bceb38b1d1",
  "platformCertificate": "-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----",
  "config": {
    "appId": "wxba6223c06417af7b",
    "symmetricKey": "otUpngOjU+nVQaWJIC3D/yMLV17RKaP6t4Ot9tbnzLY=",
    "symmetricKeySn": "fa05fe1e5bcc79b81ad5ad4b58acf787",
    "urlPath": "https://api.weixin.qq.com/wxa/getuserriskrank",
    "algorithm": "AES256_GCM",
    "signatureAlgorithm": "RSAwithSHA256"
  }
}
```

#### 获取配置示例

```http
GET /api/wechat/crypto/config-example
```

## 核心功能说明

### 加密流程

1. **生成随机nonce**：生成16字节的随机字符串（base64编码）
2. **添加安全字段**：在原始数据中添加 `_n`、`_appid`、`_timestamp` 字段
3. **JSON序列化**：将数据序列化为JSON字符串
4. **构建AAD**：按照 `urlpath|appid|timestamp|sn` 格式构建附加认证数据
5. **对称加密**：使用AES256_GCM或SM4_GCM加密数据
6. **构建签名串**：按照 `urlpath\nappid\ntimestamp\npostdata` 格式拼接待签名串
7. **生成签名**：使用RSAwithSHA256或SM2withSM3生成签名

### 解密流程

1. **验证签名**：使用平台证书验证响应签名
2. **构建AAD**：按照 `urlpath|appid|timestamp|sn` 格式构建附加认证数据
3. **对称解密**：使用AES256_GCM或SM4_GCM解密数据
4. **验证安全字段**：检查 `_appid` 和 `_timestamp` 是否匹配
5. **去除安全字段**：移除 `_n`、`_appid`、`_timestamp` 字段
6. **返回原始数据**：返回解密后的原始业务数据

### 安全验证

- **时间戳验证**：默认允许300秒的时间偏差，可配置
- **AppID验证**：验证响应中的appid与配置一致
- **签名验证**：使用平台证书验证签名有效性
- **认证标签验证**：GCM模式下的authTag验证

## 错误码

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| 40230 | API_Missing_Wechatmp_Serial | 缺少证书编号 |
| 40231 | API_Missing_Wechatmp_Timestamp | 缺少时间戳 |
| 40232 | API_Missing_Wechatmp_Signature | 缺少签名 |
| 40233 | API_Missing_Wechatmp_Appid | 缺少AppID |
| 40234 | API_Invalid_Signature | 签名错误 |
| 40235 | API_Invalid_Encrypt | 加密错误 |
| 40236 | API_Invalid_Wechatmp_Appid | 无效的AppID |
| 40237 | API_Invalid_Wechatmp_Appidmatch | AppID不匹配 |
| 40238 | API_NoExist_DevSecretSym | 未设置对称密钥 |
| 40239 | API_NoExist_DevSecretAsym | 未设置私钥 |
| 40240 | API_Expired_Wechatmp_Timestamp | 时间戳超时 |

## 注意事项

1. **密钥安全**：
   - 私钥和对称密钥必须妥善保管，不要泄露
   - 不要在代码中硬编码密钥，建议使用环境变量或配置中心
   - 定期更换密钥和证书

2. **证书更新**：
   - 平台证书有有效期，需要定期更新
   - 注意监控响应中的 `Wechatmp-Serial-Deprecated` 字段
   - 证书更换周期内，系统会同时使用新旧证书

3. **日志记录**：
   - 不要在日志中输出密钥和签名等敏感信息
   - 注意日志脱敏处理

4. **异常处理**：
   - 捕获 `WechatCryptoException` 并根据错误码进行对应处理
   - 记录详细的错误日志用于排查问题

5. **性能优化**：
   - 加密操作涉及大量计算，建议在必要时缓存加密结果
   - 注意加密操作对接口性能的影响

## 参考文档

- [微信开放平台服务端API签名加密指南](https://developers.weixin.qq.com/miniprogram/dev/server/getting_started/api_signature.html)
- [微信小程序API文档](https://developers.weixin.qq.com/miniprogram/dev/framework/server-ability/api-sign.html)

## 测试

项目中提供了单元测试 `WechatApiCryptoUtilTest`，包含：
- 配置验证测试
- 时间戳格式测试
- 时间戳超时验证测试

运行测试：

```bash
mvn test -Dtest=WechatApiCryptoUtilTest
```

## 许可证

本工具类基于微信开放平台API规范实现，仅供学习和研究使用。
