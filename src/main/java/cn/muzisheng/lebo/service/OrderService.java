package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.pear.model.Result;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    /**
     * 创建订单
     * @param userId 用户ID
     * @return 订单ID
     */
    ResponseEntity<Result<Long>> create(Long userId);
    /**
     * 获取订单基础信息
     * @param orderId 订单ID
     * @return 订单基础信息
     */
    ResponseEntity<Result<Order>> detail(Long orderId);
    /**
     * 获取订单商品信息
     * @param orderId 订单ID
     * @return 订单商品信息
     */
    ResponseEntity<Result<List<OrderItem>>> detailItem(Long orderId);
    /**
     * 基础订单信息列表
     * @param userId 用户ID
     * @return 订单列表
     */
    ResponseEntity<Result<List<Order>>> list(Long userId);
    /**
     * 修改订单状态
     * @param orderId 订单ID
     * @return 修改结果
     */
    ResponseEntity<Result<Boolean>> updateStatus(Long orderId);


}
