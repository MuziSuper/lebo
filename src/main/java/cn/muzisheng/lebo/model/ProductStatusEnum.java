package cn.muzisheng.lebo.model;

public enum ProductStatusEnum {
    SELL(0, "SELL"),
    SOLD_OUT(1, "SOLD_OUT"),
   DELETED(2, "DELETE");

    private final Integer code;
    private final String description;

    ProductStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ProductStatusEnum fromCode(Integer code) {
        for (ProductStatusEnum status : ProductStatusEnum.values()) {
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
