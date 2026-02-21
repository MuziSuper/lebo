package cn.muzisheng.lebo.utils;

/**
 * User上下文类
 * 提供生命周期的上下文管理：
 * 请求级别上下文：使用ThreadLocal，当前请求共享，请求结束后手动拦截器清理
 */
public class UserThreadUtil {

    /**
     * 用户ID ThreadLocal
     */
    private static final ThreadLocal<String> userThreadLocal = new ThreadLocal<>();


    /**
     * 设置当前登录用户ID
     * @param id 用户ID
     */
    public static void setCurrentOpenId(String id) {
        userThreadLocal.set(id);
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static String getCurrentOpenId() {
        return userThreadLocal.get();
    }

    /**
     * 移除当前登录用户ID
     */
    public static void removeCurrentOpenId() {
        userThreadLocal.remove();
    }

}
