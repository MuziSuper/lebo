package cn.muzisheng.lebo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 商品出库信息
 * 参数描述
 * productId 商品ID
 * number 增减的商品数量（正为入库，负为出库）
 * description 出入库描述
 */
@Data
@Builder
public class ProductOutDTO {
    /**
     * 商品ID
     */
    private String productId;
    /**
     * 出库商品数量，正数，请在库存更新时注意数字正负
     */
    private Long number;
    /**
     * 出入库描述
     */
    private String description;
}
