package cn.muzisheng.lebo.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static cn.muzisheng.lebo.constant.Constant.*;

public class AESUtil {
// 注意：实际应用中不应硬编码密钥

    /**
     * 加密方法
     *
     * @param plainText 明文文本
     * @return Base64编码后的加密字符串
     * @throws Exception 加密过程中可能抛出的异常
     */
    public static String encrypt(String plainText) throws Exception {
        // 获取AES加密器实例
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 根据密钥字节数组创建AES密钥规范
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        // 初始化加密器为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // 执行加密操作
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        // 将加密后的字节数组转换为Base64字符串返回
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解密方法
     *
     * @param encryptedText Base64编码的加密字符串
     * @return 解密后的原始文本
     * @throws Exception 解密过程中可能抛出的异常
     */
    public static String decrypt(String encryptedText) throws Exception {
        // 获取AES加密器实例
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 根据密钥字节数组创建AES密钥规范
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        // 初始化解密器为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 将Base64字符串解码后执行解密操作
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        // 将解密后的字节数组转换为字符串返回
        return new String(decryptedBytes);
    }
}
