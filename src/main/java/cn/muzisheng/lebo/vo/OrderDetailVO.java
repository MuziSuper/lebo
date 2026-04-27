package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class OrderDetailVO {
    private String id;
    private String nickName;
    private String homeNumber;
    private Long totalAmount;
    private Long pointNumber;
    private Long payAmount;
    private Integer payType;
    private Integer payOption;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
    private OrderItemVO[] orderItemVOS;
    public static OrderDetailVO fromOrder(Order order, List<OrderItem> orderItems, String nickName, Map<String, String> productImageMap) {
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setId(order.getId());
        orderDetailVO.setNickName(nickName);
        orderDetailVO.setHomeNumber(order.getHomeNumber());
        orderDetailVO.setTotalAmount(order.getTotalAmount());
        orderDetailVO.setPointNumber(order.getPointNumber());
        orderDetailVO.setPayAmount(order.getPayAmount());
        orderDetailVO.setPayType(order.getPayType().getCode());
        orderDetailVO.setPayOption(order.getPayOption().getCode());
        orderDetailVO.setPayTime(order.getPayTime());
        orderDetailVO.setCreateTime(order.getCreateTime());
        orderDetailVO.setEndTime(order.getEndTime());
        List<OrderItemVO> orderItemVOS = orderItems.stream()
                .map(item -> OrderItemVO.fromOrderItem(item, productImageMap.get(item.getProductId())))
                .toList();
        orderDetailVO.setOrderItemVOS(orderItemVOS.toArray(new OrderItemVO[0]));
        return orderDetailVO;
    }

}
