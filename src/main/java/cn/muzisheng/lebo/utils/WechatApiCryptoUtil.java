package cn.muzisheng.lebo.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信开放平台API加解密工具类（简化版）暂未启用，代码未审查
 *
 * 配置从环境变量读取：
 * 1. WECHAT_APP_ID - 小程序AppID
 * 2. WECHAT_SYMMETRIC_KEY - AES对称密钥（base64编码）
 * 3. WECHAT_SYMMETRIC_KEY_SN - 对称密钥编号
 * 4. WECHAT_PRIVATE_KEY_PEM - RSA私钥（PEM格式，完整-----BEGIN到-----END）
 * 5. WECHAT_ASYMMETRIC_KEY_SN - 非对称密钥编号
 * 6. WECHAT_PLATFORM_CERTIFICATE - 平台证书（PEM格式）
 * 7. WECHAT_PLATFORM_CERTIFICATE_SN - 平台证书编号
 *
 * @author MuziSuper
 * @since 2026-02-19
 */
@Log4j2
public class WechatApiCryptoUtil {

    // ========== 环境变量配置键 ==========
    private static final String ENV_APP_ID = "WECHAT_APP_ID";
    private static final String ENV_SYMMETRIC_KEY = "WECHAT_SYMMETRIC_KEY";
    private static final String ENV_SYMMETRIC_KEY_SN = "WECHAT_SYMMETRIC_KEY_SN";
    private static final String ENV_PRIVATE_KEY_PEM = "WECHAT_PRIVATE_KEY_PEM";
    private static final String ENV_ASYMMETRIC_KEY_SN = "WECHAT_ASYMMETRIC_KEY_SN";
    private static final String ENV_PLATFORM_CERTIFICATE = "WECHAT_PLATFORM_CERTIFICATE";
    private static final String ENV_PLATFORM_CERTIFICATE_SN = "WECHAT_PLATFORM_CERTIFICATE_SN";

    // ========== 常量配置 ==========
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // AES配置
    private static final int GCM_IV_LENGTH = 12; // 12字节IV
    private static final int GCM_TAG_LENGTH = 128; // 128位认证标签
    private static final int NONCE_LENGTH = 16; // 16字节随机数
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";

    // RSA配置
    private static final String RSA_SIGNATURE_ALGORITHM = "SHA256withRSA/PSS";
    private static final int RSA_SALT_LENGTH = 32; // 微信要求的salt长度

    // 分隔符
    private static final String AAD_SEPARATOR = "|";
    private static final String LINE_SEPARATOR = "\n";

    // 时间戳偏移量（秒）
    private static final int TIMESTAMP_OFFSET = 300;

    // ========== 私有构造函数 ==========
    private WechatApiCryptoUtil() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 加密请求数据
     *
     * @param originalData 原始请求数据（Map格式）
     * @param urlPath 完整的API URL路径（如：https://api.weixin.qq.com/wxa/getuserriskrank）
     * @return 包含加密数据、时间戳和签名的结果Map
     * @throws Exception 加密失败时抛出异常
     */
    public static Map<String, Object> encryptRequest(Map<String, Object> originalData, String urlPath) throws Exception {
        // 1. 验证配置
        validateEnvironmentConfig();

        // 2. 获取环境变量配置
        String appId = getEnv(ENV_APP_ID);
        String symmetricKey = getEnv(ENV_SYMMETRIC_KEY);
        String symmetricKeySn = getEnv(ENV_SYMMETRIC_KEY_SN);
        String privateKeyPem = getEnv(ENV_PRIVATE_KEY_PEM);
        String asymmetricKeySn = getEnv(ENV_ASYMMETRIC_KEY_SN);

        // 3. 生成时间戳和随机nonce
        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = generateNonce();

        // 4. 构建待加密数据（添加安全字段）
        Map<String, Object> dataToEncrypt = new LinkedHashMap<>(originalData);
        dataToEncrypt.put("_n", nonce);
        dataToEncrypt.put("_appid", appId);
        dataToEncrypt.put("_timestamp", timestamp);

        // 5. JSON序列化
        String plaintext = OBJECT_MAPPER.writeValueAsString(dataToEncrypt);

        // 6. 构建AAD（Additional Authenticated Data）
        String aad = buildAAD(urlPath, appId, timestamp, symmetricKeySn);

        // 7. AES256_GCM加密
        byte[] iv = generateIv();
        Map<String, String> encryptedData = aesGcmEncrypt(
                plaintext.getBytes(StandardCharsets.UTF_8),
                aad.getBytes(StandardCharsets.UTF_8),
                iv,
                symmetricKey
        );

        // 8. 构建待签名串（微信格式：urlpath\nappid\ntimestamp\npostdata）
        String postData = OBJECT_MAPPER.writeValueAsString(encryptedData);
        String signaturePayload = buildSignaturePayload(urlPath, appId, timestamp, postData);

        // 9. RSAwithSHA256签名
        String signature = rsaSign(signaturePayload.getBytes(StandardCharsets.UTF_8), privateKeyPem);

        // 10. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("encryptedData", encryptedData);   // 包含iv、data、authtag
        result.put("timestamp", timestamp);
        result.put("signature", signature);
        result.put("appId", appId);
        result.put("keySn", asymmetricKeySn);
        result.put("urlPath", urlPath);

        // 11. 构建HTTP头（方便使用）
        Map<String, String> headers = new HashMap<>();
        headers.put("Wechatmp-Appid", appId);
        headers.put("Wechatmp-TimeStamp", String.valueOf(timestamp));
        headers.put("Wechatmp-Signature", signature);
        result.put("headers", headers);

        log.info("加密请求数据成功，时间戳: {}, URL: {}", timestamp, urlPath);
        return result;
    }

    /**
     * 解密响应数据
     *
     * @param encryptedData 加密数据Map（必须包含iv、data、authtag字段）
     * @param timestamp 响应时间戳（从Wechatmp-TimeStamp头获取）
     * @param signature 响应签名（从Wechatmp-Signature头获取）
     * @param certificateSn 平台证书编号（从Wechatmp-Serial头获取）
     * @param urlPath 完整的API URL路径
     * @return 解密后的原始数据Map
     * @throws Exception 解密失败时抛出异常
     */
    public static Map<String, Object> decryptResponse(
            Map<String, String> encryptedData,
            long timestamp,
            String signature,
            String certificateSn,
            String urlPath) throws Exception {

        // 1. 验证配置
        validateEnvironmentConfig();

        // 2. 获取环境变量配置
        String appId = getEnv(ENV_APP_ID);
        String symmetricKey = getEnv(ENV_SYMMETRIC_KEY);
        String symmetricKeySn = getEnv(ENV_SYMMETRIC_KEY_SN);
        String platformCertificate = getEnv(ENV_PLATFORM_CERTIFICATE);
        String platformCertificateSn = getEnv(ENV_PLATFORM_CERTIFICATE_SN);

        // 3. 验证证书编号
        if (!platformCertificateSn.equals(certificateSn)) {
            log.warn("平台证书编号不匹配，配置: {}，响应: {}", platformCertificateSn, certificateSn);
            // 这里应该检查是否匹配即将过期的证书（如果有）
        }

        // 4. 构建待验证签名字符串
        String respData = OBJECT_MAPPER.writeValueAsString(encryptedData);
        String signaturePayload = buildSignaturePayload(urlPath, appId, timestamp, respData);

        // 5. 验证RSA签名
        boolean signatureValid = rsaVerify(
                signaturePayload.getBytes(StandardCharsets.UTF_8),
                BASE64_DECODER.decode(signature),
                platformCertificate
        );

        if (!signatureValid) {
            throw new SecurityException("签名验证失败");
        }

        // 6. 构建AAD
        String aad = buildAAD(urlPath, appId, timestamp, symmetricKeySn);

        // 7. AES256_GCM解密
        byte[] decryptedBytes = aesGcmDecrypt(
                encryptedData.get("data"),
                encryptedData.get("iv"),
                encryptedData.get("authtag"),
                aad.getBytes(StandardCharsets.UTF_8),
                symmetricKey
        );

        // 8. 解析解密后的数据
        String decryptedJson = new String(decryptedBytes, StandardCharsets.UTF_8);
        Map<String, Object> decryptedData = OBJECT_MAPPER.readValue(decryptedJson, Map.class);

        // 9. 验证安全字段
        validateSecurityFields(decryptedData, appId, timestamp);

        // 10. 移除安全字段
        decryptedData.remove("_n");
        decryptedData.remove("_appid");
        decryptedData.remove("_timestamp");

        log.info("解密响应数据成功，时间戳: {}, URL: {}", timestamp, urlPath);
        return decryptedData;
    }

    // ========== 核心加密方法 ==========

    /**
     * AES256_GCM加密
     */
    private static Map<String, String> aesGcmEncrypt(byte[] plaintext, byte[] aad, byte[] iv, String symmetricKeyBase64)
            throws Exception {

        byte[] keyBytes = BASE64_DECODER.decode(symmetricKeyBase64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
        cipher.updateAAD(aad);

        byte[] ciphertext = cipher.doFinal(plaintext);

        // GCM模式下，最后16字节是authTag
        int ciphertextLength = ciphertext.length - 16;
        byte[] dataBytes = new byte[ciphertextLength];
        byte[] authTagBytes = new byte[16];

        System.arraycopy(ciphertext, 0, dataBytes, 0, ciphertextLength);
        System.arraycopy(ciphertext, ciphertextLength, authTagBytes, 0, 16);

        Map<String, String> result = new HashMap<>();
        result.put("iv", BASE64_ENCODER.encodeToString(iv));
        result.put("data", BASE64_ENCODER.encodeToString(dataBytes));
        result.put("authtag", BASE64_ENCODER.encodeToString(authTagBytes));

        return result;
    }

    /**
     * AES256_GCM解密
     */
    private static byte[] aesGcmDecrypt(String dataBase64, String ivBase64, String authTagBase64,
                                        byte[] aad, String symmetricKeyBase64) throws Exception {

        byte[] keyBytes = BASE64_DECODER.decode(symmetricKeyBase64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = BASE64_DECODER.decode(ivBase64);
        byte[] data = BASE64_DECODER.decode(dataBase64);
        byte[] authTag = BASE64_DECODER.decode(authTagBase64);

        // 合并data和authTag
        byte[] ciphertext = new byte[data.length + authTag.length];
        System.arraycopy(data, 0, ciphertext, 0, data.length);
        System.arraycopy(authTag, 0, ciphertext, data.length, authTag.length);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
        cipher.updateAAD(aad);

        return cipher.doFinal(ciphertext);
    }

    /**
     * RSAwithSHA256签名
     */
    private static String rsaSign(byte[] data, String privateKeyPem) throws Exception {
        // 清理PEM格式
        String cleanPem = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] privateKeyBytes = BASE64_DECODER.decode(cleanPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 使用PSS填充方式，salt长度为32（微信要求）
        Signature signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
        signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1",
                MGF1ParameterSpec.SHA256, RSA_SALT_LENGTH, 1));
        signature.initSign(privateKey);
        signature.update(data);

        byte[] signBytes = signature.sign();
        return BASE64_ENCODER.encodeToString(signBytes);
    }

    /**
     * RSAwithSHA256验签
     */
    private static boolean rsaVerify(byte[] data, byte[] signatureBytes, String certificatePem) throws Exception {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream certInputStream = new ByteArrayInputStream(
                certificatePem.getBytes(StandardCharsets.UTF_8)
        );
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certInputStream);
        PublicKey publicKey = cert.getPublicKey();

        Signature signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
        signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1",
                MGF1ParameterSpec.SHA256, RSA_SALT_LENGTH, 1));
        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(signatureBytes);
    }

    // ========== 辅助方法 ==========

    /**
     * 生成随机nonce（16字节base64字符串）
     */
    private static String generateNonce() {
        byte[] nonce = new byte[NONCE_LENGTH];
        SECURE_RANDOM.nextBytes(nonce);
        String base64Nonce = BASE64_ENCODER.encodeToString(nonce);
        // 移除base64填充符=
        return base64Nonce.replace("=", "");
    }

    /**
     * 生成GCM初始化向量（12字节）
     */
    private static byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * 构建AAD字符串
     */
    private static String buildAAD(String urlPath, String appId, long timestamp, String keySn) {
        return String.join(AAD_SEPARATOR, urlPath, appId, String.valueOf(timestamp), keySn);
    }

    /**
     * 构建签名payload字符串
     */
    private static String buildSignaturePayload(String urlPath, String appId, long timestamp, String postData) {
        return String.join(LINE_SEPARATOR, urlPath, appId, String.valueOf(timestamp), postData);
    }

    /**
     * 验证安全字段
     */
    private static void validateSecurityFields(Map<String, Object> data, String expectedAppId, long expectedTimestamp)
            throws SecurityException {

        // 验证_appid
        Object appIdObj = data.get("_appid");
        if (appIdObj == null || !expectedAppId.equals(appIdObj.toString())) {
            throw new SecurityException("appid验证失败，期望: " + expectedAppId + "，实际: " + appIdObj);
        }

        // 验证_timestamp
        Object timestampObj = data.get("_timestamp");
        long responseTimestamp = 0;
        if (timestampObj instanceof Number) {
            responseTimestamp = ((Number) timestampObj).longValue();
        } else if (timestampObj != null) {
            responseTimestamp = Long.parseLong(timestampObj.toString());
        }

        if (Math.abs(expectedTimestamp - responseTimestamp) > TIMESTAMP_OFFSET) {
            throw new SecurityException("时间戳验证失败，期望: " + expectedTimestamp +
                    "，实际: " + responseTimestamp + "，偏移: " +
                    Math.abs(expectedTimestamp - responseTimestamp));
        }
    }

    /**
     * 验证环境变量配置
     */
    private static void validateEnvironmentConfig() throws IllegalStateException {
        String[] requiredEnvs = {
                ENV_APP_ID, ENV_SYMMETRIC_KEY, ENV_SYMMETRIC_KEY_SN,
                ENV_PRIVATE_KEY_PEM, ENV_ASYMMETRIC_KEY_SN,
                ENV_PLATFORM_CERTIFICATE, ENV_PLATFORM_CERTIFICATE_SN
        };

        for (String envKey : requiredEnvs) {
            String value = System.getenv(envKey);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalStateException("环境变量未配置: " + envKey);
            }
        }
    }

    /**
     * 获取环境变量（带默认值）
     */
    private static String getEnv(String key) {
        return System.getenv(key);
    }

    /**
     * 获取环境变量（带默认值）
     */
    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    // ========== 测试方法（仅用于演示） ==========

    /**
     * 测试加密解密流程
     */
    public static void testEncryptDecrypt() throws Exception {
        // 假设这是原始请求数据
        Map<String, Object> originalData = new HashMap<>();
        originalData.put("openid", "oEWzBfmdLqhFS2mTXCo2E4Y9gJAM");
        originalData.put("scene", 0);
        originalData.put("client_ip", "127.0.0.1");

        String urlPath = "https://api.weixin.qq.com/wxa/getuserriskrank";

        log.info("=== 测试加密解密流程 ===");

        try {
            // 1. 加密请求
            log.info("1. 开始加密请求数据...");
            Map<String, Object> encryptedResult = encryptRequest(originalData, urlPath);
            log.info("加密成功，时间戳: {}", encryptedResult.get("timestamp"));

            // 2. 模拟获取加密数据和签名
            @SuppressWarnings("unchecked")
            Map<String, String> encryptedData = (Map<String, String>) encryptedResult.get("encryptedData");
            long timestamp = (long) encryptedResult.get("timestamp");
            String signature = (String) encryptedResult.get("signature");
            String certificateSn = getEnv(ENV_PLATFORM_CERTIFICATE_SN);

            // 3. 解密响应（模拟）
            log.info("2. 开始解密响应数据...");
            Map<String, Object> decryptedData = decryptResponse(encryptedData, timestamp, signature, certificateSn, urlPath);

            log.info("3. 解密成功，原始数据: {}", decryptedData);
            log.info("4. 原始数据与解密数据对比: {}",
                    originalData.equals(decryptedData) ? "匹配" : "不匹配");

        } catch (Exception e) {
            log.error("测试失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 主方法（仅用于测试）
     */
    public static void main(String[] args) {
        try {
            testEncryptDecrypt();
        } catch (Exception e) {
            log.error("加解密测试失败", e);
            System.exit(1);
        }
    }
}
