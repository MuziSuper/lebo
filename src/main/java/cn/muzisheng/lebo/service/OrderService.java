package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderUpdateDTO;
import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.OrderDetailVO;
import cn.muzisheng.lebo.vo.OrderInfoVO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    /**
     * 创建订单
     * @return 订单ID
     */
    ResponseEntity<Result<String>> create();
    /**
     * 创建订单
     * @param orderAddDTO 订单信息
     * @return 订单ID
     */
    ResponseEntity<Result<String>> submit(OrderAddDTO orderAddDTO);
    /**
     * 取消订单
     * @param orderAddDTO 订单信息
     * @return 订单ID
     */
    ResponseEntity<Result<String>> cancel(OrderAddDTO orderAddDTO);
    /**
     * 获取订单详细信息
     * @param orderId 订单ID
     * @return 订单商品信息
     */
    ResponseEntity<Result<OrderDetailVO>> detail(Long orderId);
    /**
     * 基础订单信息列表
     * @return 订单列表
     */
    ResponseEntity<Result<List<OrderInfoVO>>> orderInfoList();
    /**
     * 修改订单状态
     * @param orderUpdateDTO 订单信息
     * @return 修改结果
     */
    ResponseEntity<Result<Boolean>> updateOrder(OrderUpdateDTO orderUpdateDTO);


}
