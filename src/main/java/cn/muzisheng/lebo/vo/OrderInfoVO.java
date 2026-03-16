package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderInfoVO {
    /**
     * 订单ID
     */
    private String id;
    /**
     * 订单金额
     */
    private Long totalAmount;
    /**
     * 实际支付金额
     */
    private Long payAmount;
    /**
     * 支付状态，1: 未支付, 2: 已支付,3: 支付失败, 4: 已退款
     */
    private Integer payType;
    /**
     * 支付选项，0: 线下支付, 1: 微信支付, 2: 支付宝支付
     */
    private Integer payOption;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 订单结束时间
     */
    private LocalDateTime endTime;
    public static OrderInfoVO fromOrder(Order order) {
        return OrderInfoVO.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .payType(order.getPayType().getCode())
                .payOption(order.getPayOption().getCode())
                .payTime(order.getPayTime())
                .createTime(order.getCreateTime())
                .endTime(order.getEndTime())
                .build();
    }
}
