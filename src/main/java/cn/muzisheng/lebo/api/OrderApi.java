package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderBossListDTO;
import cn.muzisheng.lebo.dto.OrderListDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.OrderService;
import cn.muzisheng.lebo.vo.OrderDetailVO;
import cn.muzisheng.lebo.vo.OrderInfoVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderApi {
    private final OrderService orderService;
    public OrderApi(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 用户创建订单，携带选购商品信息，填充订单商品信息、订单状态为未支付、订单创建时间、订单金额、订单用户ID
     * @param orderAddDTO 订单信息（商品列表）
     * @return 订单ID
     */
    @PostMapping("/create")
    public ResponseEntity<Result<String>> create(@RequestBody OrderAddDTO orderAddDTO) {
        return orderService.create(orderAddDTO);
    }
    /**
     * 用户确认支付，传入支付方式，修改订单状态为已支付、填充支付方式、实际支付金额、支付时间
     * @param orderAddDTO 订单信息（订单ID、支付方式）
     * @return 订单ID
     */
    @PostMapping("/submit")
    public ResponseEntity<Result<String>> submit(@RequestBody OrderAddDTO orderAddDTO) {
        return orderService.submit(orderAddDTO);
    }
    /**
     * 用户取消支付，修改订单状态为支付失败，填充订单结束时间、实际支付金额为0
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    @PostMapping("/cancel")
    public ResponseEntity<Result<Boolean>> cancel(@RequestParam("orderId") String orderId) {
        return orderService.cancel(orderId);
    }
    /**
     * 用户获取订单列表，筛选条件为订单状态
     * @param orderListDTO 订单列表
     * @return 订单详情
     */
    @PostMapping("/orderInfolist")
    public ResponseEntity<Result<List<OrderInfoVO>>> orderInfoList(OrderListDTO orderListDTO) {
        return orderService.orderInfoList(orderListDTO);
    }

    /**
     * 商家获取订单列表，筛选条件为订单状态,订单创建时间区间，订单结束时间区间，订单支付方式，订单ID
     * @param orderBossListDTO 订单列表
     * @return 订单详情
     */
    @PostMapping("/bossOrderInfolist")
    public ResponseEntity<Result<List<OrderInfoVO>>> bossOrderInfoList(OrderBossListDTO orderBossListDTO) {
        return orderService.orderBossInfoList(orderBossListDTO);
    }
    /**
     * 商家确认订单结束，修改订单状态为已结束，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单Id
     * @return 订单ID
     */
    @PostMapping("/over")
    public ResponseEntity<Result<Boolean>> orderOver(@RequestParam("orderId") String orderId) {
        return orderService.orderOver(orderId);
    }

    /**
     * 商家拒绝接单，修改订单状态为已退款，最终支付价格为0，商品库存退回，返回是否成功
     * @param orderId 订单Id
     * @return 是否成功
     */
    @PostMapping("/reject")
    public ResponseEntity<Result<Boolean>> orderReject(@RequestParam("orderId") String orderId) {
        return orderService.orderReject(orderId);
    }

    /**
     * 获取订单详情
     * @param orderId 订单 ID
     * @return 订单详情
     */
    @PostMapping("/detail")
    public ResponseEntity<Result<OrderDetailVO>> detail(@RequestParam("orderId") String orderId) {
        return orderService.detail(orderId);
    }


}
