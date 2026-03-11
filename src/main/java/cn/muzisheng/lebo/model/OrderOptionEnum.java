package cn.muzisheng.lebo.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderOptionEnum {
    ACTIVE(0, "OFFLINE"),
    WECHAT(1, "WECHAT"),
    ALIPAY(2, "ALIPAY");
    @EnumValue
    private final Integer code;
    private final String description;

    OrderOptionEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static OrderOptionEnum fromCode(Integer code) {
        for (OrderOptionEnum status : OrderOptionEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    public static boolean containsCode(Integer code) {
        return fromCode(code) != null;
    }

    @Override
    public String toString() {
        return "code: "+code+"\ndescription: "+description+"\n";
    }
}
