package cn.muzisheng.lebo.dto;

import lombok.Data;

/**
 * 订单支付DTO
 */
@Data
public class OrderPayDTO {
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 支付方式，0: 线下收款, 1: 微信，2: 支付宝
     */
    private Integer orderOptionCode;

}