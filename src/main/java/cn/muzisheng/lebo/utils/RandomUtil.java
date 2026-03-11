package cn.muzisheng.lebo.utils;

import java.util.UUID;

public class RandomUtil {
    /**
     * 生成32位的UUID
     **/
    public static String generateId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
    /**
     * 生成32位
     */

}
