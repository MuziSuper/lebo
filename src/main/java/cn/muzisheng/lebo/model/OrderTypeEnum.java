package cn.muzisheng.lebo.model;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderTypeEnum {
    NONPAYMENT(1, "NONPAYMENT"),
    PAID(2, "PAID"),
    FAILURE(3, "FAILURE"),
    REFUNDED(4, "REFUNDED"),
    OVER(5, "OVER");
    @EnumValue
    private final Integer code;
    private final String description;

    OrderTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    /**
     * 根据code获取枚举
     * @param code 枚举code
     * @return 枚举
     */

    public static OrderTypeEnum fromCode(Integer code) {
        for (OrderTypeEnum status : OrderTypeEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean contains(Integer code) {
        if(code==null) return false;
        for (OrderTypeEnum status : OrderTypeEnum.values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "code: "+code+"\ndescription: "+description+"\n";
    }
}
