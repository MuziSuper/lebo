package cn.muzisheng.lebo.dto;

import lombok.Builder;
import lombok.Data;
/**
 * 商品出入库信息
 * 参数描述
 * productId 商品ID
 * number 增减的商品数量（正为入库，负为出库）
 * description 出入库描述
 */
@Data
@Builder
public class ProductInOutDTO {
    /**
     * 商品ID
     */
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
