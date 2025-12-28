package cn.muzisheng.lebo.model;

public enum AccountStatusEnum {
    ACTIVE(0, "active"),
    INACTIVE(1, "inactive"),
    SUSPENDED(2, "suspended"),
    BANNED(3, "banned"),
    DELETED(4, "delete");

    private final Integer code;
    private final String description;

    AccountStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AccountStatusEnum fromCode(Integer code) {
        for (AccountStatusEnum status : AccountStatusEnum.values()) {
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
