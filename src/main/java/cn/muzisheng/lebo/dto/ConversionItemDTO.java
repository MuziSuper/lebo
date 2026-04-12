package cn.muzisheng.lebo.dto;
import lombok.Data;
/**
 * 积分兑换商品项DTO
 * 包含商品ID和数量(出库数量，为正数，需要注意正负数)
 *
 */
@Data
public class ConversionItemDTO {
    /**
     * 商品ID
     */
    private String productId;
    /**
     * 商品数量
     */
    private Long number;

    
}
