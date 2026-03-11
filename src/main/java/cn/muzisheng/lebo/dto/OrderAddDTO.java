package cn.muzisheng.lebo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderAddDTO {
    /**
     * 支付方式，0: 线下收款, 1: 微信，2: 支付宝
     */
    private Integer OrderTypeCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
//    /**
//     * 是否会员优惠
//     */
//    private Boolean memberDiscount;
    /**
     * 购买商品列表
     */
    private List<OrderProductItemDTO> orderProductItemDTOS;
}
