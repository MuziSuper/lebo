package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDetailVO {
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
    /**
     * 订单商品列表
     */
    private OrderItemVO[] orderItemVOS;
    public static OrderDetailVO fromOrder(Order order, List<OrderItem> orderItems) {
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setId(order.getId());
        orderDetailVO.setTotalAmount(order.getTotalAmount());
        orderDetailVO.setPayAmount(order.getPayAmount());
        orderDetailVO.setPayType(order.getPayType().getCode());
        orderDetailVO.setPayOption(order.getPayOption().getCode());
        orderDetailVO.setPayTime(order.getPayTime());
        orderDetailVO.setCreateTime(order.getCreateTime());
        orderDetailVO.setEndTime(order.getEndTime());
        List<OrderItemVO> orderItemVOS = orderItems.stream().map(OrderItemVO::fromOrderItem).toList();
        orderDetailVO.setOrderItemVOS(orderItemVOS.toArray(new OrderItemVO[0]));
        return orderDetailVO;
    }

}
