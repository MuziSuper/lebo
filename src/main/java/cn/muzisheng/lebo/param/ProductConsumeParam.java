package cn.muzisheng.lebo.param;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductConsumeParam {
    private String productId;
    /**
     * 新的商品数量
     */
    private Long newNumber;
}
