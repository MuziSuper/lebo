package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderBossListDTO;
import cn.muzisheng.lebo.dto.OrderListDTO;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.OrderDetailVO;
import cn.muzisheng.lebo.vo.OrderInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService extends IService<Order> {
    /**
     * 用户点击支付创建订单，订单状态为未支付，无需确认商品是否足够，前端已经对商品销售进行限制
     * 只需返回订单ID，即可在确认支付订单页面展示订单的商品信息
     * @return 订单ID
     */
    ResponseEntity<Result<String>> create();
    /**
     * 用户点击确认支付, 服务端订单更新订单支付时间，并进行商品出库
     * 如果检测订单商品库存不足则状态更新为支付失败，返回报错原因，如果订单确认支付时间超过5分钟则状态更新为支付失败，返回报错原因
     * 如果订单商品充足则状态更新为已支付，填充支付时间
     * 返回是否成功
     * @param orderAddDTO 订单信息
     * @return 订单ID
     */
    ResponseEntity<Result<String>> submit(OrderAddDTO orderAddDTO);
    /**
     * 用户在订单确认支付页面点击了取消，则修改订单状态为支付失败，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    ResponseEntity<Result<Boolean>> cancel(Long orderId);
    /**
     * 获取订单详细信息
     * @param orderId 订单ID
     * @return 订单商品信息
     */
    ResponseEntity<Result<OrderDetailVO>> detail(Long orderId);
    /**
     * 用户获取订单列表，筛选条件为订单状态
     * @param orderListDTO 订单列表
     * @return 订单详情
     */
    ResponseEntity<Result<List<OrderInfoVO>>> orderInfoList(OrderListDTO orderListDTO);
    /**
     * 商家获取订单列表，筛选条件为订单状态,订单创建时间区间，订单结束时间区间，订单支付方式，订单ID
     * @param orderBossListDTO 订单列表
     * @return 订单详情
     */
    ResponseEntity<Result<List<OrderInfoVO>>> orderBossInfoList(OrderBossListDTO orderBossListDTO);
    /**
     * 商家确认订单结束，修改订单状态为已结束，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单Id
     * @return 订单ID
     */
    ResponseEntity<Result<Boolean>> orderOver(String orderId);


}
