package cn.muzisheng.lebo.model;

public enum OrderTypeEnum {
    NONPAYMENT(1, "NONPAYMENT"),
    PAID(2, "PAID"),
    FAILURE(3, "FAILURE"),
    REFUNDED(4, "REFUNDED");

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

    public static OrderTypeEnum fromCode(Integer code) {
        for (OrderTypeEnum status : OrderTypeEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown account status code: " + code);
    }

    @Override
    public String toString() {
        return "code: "+code+"\ndescription: "+description+"\n";
    }
}
