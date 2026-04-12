package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderBossListDTO;
import cn.muzisheng.lebo.dto.OrderListDTO;
import cn.muzisheng.lebo.dto.OrderPayDTO;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.OrderDetailVO;
import cn.muzisheng.lebo.vo.OrderInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService extends IService<Order> {
    /**
     * 用户创建订单，携带选购商品信息，填充订单商品信息、订单状态为未支付、订单创建时间、订单金额、订单用户ID
     * @param orderAddDTO 订单信息（商品列表）
     * @return 订单ID
     */
    ResponseEntity<Result<String>> create(OrderAddDTO orderAddDTO);
    /**
     * 用户确认支付，传入支付方式，修改订单状态为已支付、填充支付方式、实际支付金额、支付时间
     * @param orderAddDTO 订单信息（订单ID、支付方式）
     * @return 订单ID
     */
    ResponseEntity<Result<String>> submit(OrderPayDTO orderAddDTO);
    /**
     * 用户取消支付，修改订单状态为支付失败，填充订单结束时间、实际支付金额为0
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    ResponseEntity<Result<Boolean>> cancel(String orderId);
    /**
     * 获取订单详细信息
     * @param orderId 订单ID
     * @return 订单商品信息
     */
    ResponseEntity<Result<OrderDetailVO>> detail(String orderId);
    /**
     * 用户获取订单列表，筛选条件为订单状态
     * @param orderListDTO 订单列表
     * @return 订单详情
     */
    ResponseEntity<Result<List<OrderInfoVO>>> orderInfoList(OrderListDTO orderListDTO);
    /**
     * 商家获取订单列表，筛选条件为订单状态,订单创建时间区间，订单结束时间区间，订单支付方式，订单ID
     * @param orderBossListDTO 订单列表查询条件
     * @return 订单分页数据
     */
    ResponseEntity<Result<IPage<OrderInfoVO>>> orderBossInfoList(OrderBossListDTO orderBossListDTO);
    /**
     * 商家确认订单结束，修改订单状态为已结束，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单Id
     * @return 订单ID
     */
    ResponseEntity<Result<Boolean>> orderOver(String orderId);
    /**
     * 商家拒绝接单，修改订单状态为已退款，最终支付价格为0，商品库存退回，返回是否成功
     * @param orderId 订单Id
     * @return 是否成功
     */
    ResponseEntity<Result<Boolean>> orderReject(String orderId);

}
