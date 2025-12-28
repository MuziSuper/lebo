package cn.muzisheng.lebo.model;

public enum OrderOptionEnum {    ACTIVE(0, "active"),
    WECHAT(1, "WECHAT"),
    ALIPAY(2, "ALIPAY"),
    BANKCARD(3, "BANKCARD");

    private final Integer code;
    private final String description;

    OrderOptionEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderOptionEnum fromCode(Integer code) {
        for (OrderOptionEnum status : OrderOptionEnum.values()) {
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
