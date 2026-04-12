package cn.muzisheng.lebo.model;

import lombok.Getter;

@Getter
public enum PointRecordTypeEnum {
    ORDER_PAY(1, "订单结算"),
    PRODUCT_CONVERT(2, "商品兑换"),
    DAY_SIGN_IN(3, "每日签到"),
    LOTTERY_AWARD(4, "抽奖奖励");
    private final Integer code;
    private final String description;
    PointRecordTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


}
