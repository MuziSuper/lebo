package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderBossListDTO;
import cn.muzisheng.lebo.dto.OrderListDTO;
import cn.muzisheng.lebo.dto.OrderUpdateDTO;
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
     * 用户点击支付创建订单，订单状态为未支付，无需确认商品是否足够，前端已经对商品销售进行限制
     * 只需返回订单ID，即可在确认支付订单页面展示订单的商品信息
     * @return 订单ID
     */
    @PostMapping("/create")
    public ResponseEntity<Result<String>> create() {
        return orderService.create();
    }
    /**
     * 用户点击确认支付, 服务端订单更新订单支付时间，并进行商品出库
     * 如果检测订单商品库存不足则状态更新为支付失败，返回报错原因，如果订单确认支付时间超过5分钟则状态更新为支付失败，返回报错原因
     * 如果订单商品充足则状态更新为已支付，填充支付时间
     * 返回是否成功
     * @param orderAddDTO 订单信息
     * @return 订单ID
     */
    @PostMapping("/submit")
    public ResponseEntity<Result<String>> submit(@RequestBody OrderAddDTO orderAddDTO) {
        return orderService.submit(orderAddDTO);
    }
    /**
     * 用户在订单确认支付页面点击了取消，则修改订单状态为支付失败，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单ID
     * @return 订单ID
     */
    @PostMapping("/cancel")
    public ResponseEntity<Result<Boolean>> cancel(@RequestParam Long orderId) {
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
    public ResponseEntity<Result<Boolean>> orderOver(@RequestParam String orderId) {
        return orderService.orderOver(orderId);
    }

    /**
     * 获取订单详情
     * @param orderId 订单 ID
     * @return 订单详情
     */
    @PostMapping("/detail")
    public ResponseEntity<Result<OrderDetailVO>> detail(@RequestParam("orderId") Long orderId) {
        return orderService.detail(orderId);
    }


}
