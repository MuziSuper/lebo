package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.exception.OrderException;

import java.util.List;

public interface OrderItemService {
    /**
     * 创建
     * @param orderItem 订单商品信息
     * @return 创建结果
     * @throws OrderException 订单异常
     */
    boolean create(OrderItem orderItem) throws OrderException;
    /**
     * 获取订单商品列表
     * @param orderId 订单 ID
     * @return 订单商品列表
     */
    List<OrderItem> list(Long orderId);
}
