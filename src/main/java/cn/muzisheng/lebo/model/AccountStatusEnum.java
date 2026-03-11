package cn.muzisheng.lebo.model;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 账户状态枚举
 * 定义系统中所有可能的账户状态
 */
public enum AccountStatusEnum {
    /**
     * 活跃状态 - 账户正常使用中
     */
    ACTIVE(0, "active"),
    /**
     * 非活跃状态 - 账户存在但暂时未使用
     */
    INACTIVE(1, "inactive"),
    /**
     * 暂停状态 - 账户因违规等原因被暂时限制
     */
    SUSPENDED(2, "suspended"),
    /**
     * 封禁状态 - 账户因严重违规被永久限制
     */
    BANNED(3, "banned"),
    /**
     * 删除状态 - 账户已被用户或管理员删除
     * 注意：description字段值为"delete"，表示已删除状态
     */
    DELETED(4, "delete");

    /**
     * 状态码 - 用于数据库存储和程序逻辑判断
     */
    @EnumValue
    private final Integer code;
    /**
     * 状态描述 - 用于显示和日志记录
     */
    private final String description;

    /**
     * 枚举构造函数
     *
     * @param code        状态码
     * @param description 状态描述
     */
    AccountStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取对应的枚举实例
     *
     * @param code 状态码
     * @return 对应的AccountStatusEnum实例
     * @throws IllegalArgumentException 当传入的状态码不存在时抛出此异常
     */
    public static AccountStatusEnum fromCode(Integer code) {
        // 遍历所有枚举值，查找匹配的状态码
        for (AccountStatusEnum status : AccountStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown account status code: " + code);
    }

    /**
     * 重写toString方法，提供更友好的字符串表示
     *
     * @return 格式化的枚举信息字符串
     */
    @Override
    public String toString() {
        return "code: " + code + "\ndescription: " + description + "\n";
    }
}
