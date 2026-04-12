package cn.muzisheng.lebo.dto;

import lombok.Data;

import java.util.List;

/**
 * 积分兑换DTO
 * 包含商品ID、数量等信息
 * @see ConversionItemDTO
 */
@Data
public class ConversionDTO {
    /**
     * 商品列表
     */
    private List<ConversionItemDTO> items;
    /**
     * 积分兑换描述
     */
    private String description;
    /**
     * 用户openid
     */
    private String openId;
}
