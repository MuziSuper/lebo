package cn.muzisheng.lebo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInOutDTO {
    private String productId;
    /**
     * 增减的商品数量
     */
    private Long number;
    /**
     * 出入库描述
     */
    private String description;
}
