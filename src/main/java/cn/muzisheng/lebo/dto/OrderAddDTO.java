package cn.muzisheng.lebo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderAddDTO {
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 支付方式，0: 线下收款, 1: 微信，2: 支付宝
     */
    private Integer orderOptionCode;

    /**
     * 购买商品列表
     */
    private List<ProductInOutDTO> productInOutDTOList;
}
