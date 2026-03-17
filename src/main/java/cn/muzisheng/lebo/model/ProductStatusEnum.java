package cn.muzisheng.lebo.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ProductStatusEnum {
    SELL(0, "SELL"),
    SOLD_OUT(1, "SOLD_OUT"),
   DELETED(2, "DELETE");
    @EnumValue
    private final Integer code;
    private final String description;

    ProductStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     * @param code
     * @return
     */
    public static ProductStatusEnum fromCode(Integer code) {
        for (ProductStatusEnum status : ProductStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否存在某code
     * @param code
     * @return 存在返回true，不存在返回false
     */
    public static boolean contains(Integer code) {
        if(code==null) return false;
        return fromCode(code) != null;
    }
    @Override
    public String toString() {
        return "code: "+code+"\ndescription: "+description+"\n";
    }
}
