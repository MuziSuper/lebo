package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderProductItemDTO;
import cn.muzisheng.lebo.dto.OrderUpdateDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.OrderException;
import cn.muzisheng.lebo.mapper.OrderMapper;
import cn.muzisheng.lebo.model.*;
import cn.muzisheng.lebo.service.OrderItemService;
import cn.muzisheng.lebo.service.OrderService;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.lebo.utils.RandomUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import cn.muzisheng.lebo.vo.OrderDetailVO;
import cn.muzisheng.lebo.vo.OrderInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    private final ProductService productService;

    private final OrderItemService orderItemService;
    public OrderServiceImpl(ProductService productService, OrderItemService orderItemService) {
        this.productService = productService;
        this.orderItemService = orderItemService;
    }

    /**
     * 用户点击确认支付, 服务端订单更新订单支付时间，并进行商品出库
     * 如果检测订单商品库存不足则状态更新为支付失败，返回报错原因，如果订单确认支付时间超过5分钟则状态更新为支付失败，返回报错原因
     * 如果订单商品充足则状态更新为已支付，填充支付时间
     * 返回是否成功
     * @param orderAddDTO 订单信息
     * @return 订单ID
     */
    @Override
    @Transactional(rollbackFor = OrderException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<String>> submit(OrderAddDTO orderAddDTO) {
        Response<String> response = new Response<>();
        
        // 参数校验
        if (orderAddDTO == null || orderAddDTO.getOrderId() == null) {
            log.error("订单信息不能为空");
            throw new OrderException("订单信息不能为空");
        }
        
        String orderId = orderAddDTO.getOrderId();
        
        // 查询订单
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId).last("FOR UPDATE"));
        if (order == null) {
            log.error("订单不存在, orderId: " + orderId);
            throw new OrderException("订单不存在");
        }
        
        // 验证订单状态
        if (order.getPayType() != OrderTypeEnum.NONPAYMENT) {
            log.error("订单状态异常, orderId: " + orderId);
            throw new OrderException("订单状态异常");
        }
        
        // 检查订单创建时间是否超过5分钟
        if (order.getCreateTime() != null && 
            order.getCreateTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            // 更新订单状态为支付失败
            order.setPayType(OrderTypeEnum.FAILURE);
            order.setEndTime(LocalDateTime.now());
            this.updateById(order);
            log.error("订单超时, orderId: " + orderId);
            throw new OrderException("订单超时，请重新下单");
        }
        
        // 校验订单商品
        if (orderAddDTO.getOrderProductItemDTOS() == null || orderAddDTO.getOrderProductItemDTOS().isEmpty()) {
            log.error("订单商品不能为空");
            throw new OrderException("订单商品不能为空");
        }
        
        // 设置支付方式
        if (orderAddDTO.getOrderOptionCode() != null) {
            if (!OrderOptionEnum.contains(orderAddDTO.getOrderOptionCode())) {
                log.error("订单选项不存在");
                throw new OrderException("订单选项不存在");
            }
            order.setPayOption(OrderOptionEnum.fromCode(orderAddDTO.getOrderOptionCode()));
        }
        
        // 处理商品出库
        long totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();
        
        for (OrderProductItemDTO orderProductItemDTO : orderAddDTO.getOrderProductItemDTOS()) {
            // 商品参数校验
            if (orderProductItemDTO.getProductId() == null || 
                orderProductItemDTO.getQuantity() == null || 
                orderProductItemDTO.getQuantity() <= 0) {
                log.error("订单商品传参异常");
                throw new OrderException("订单商品传参异常");
            }
            
            // 查询商品库存（使用悲观锁）
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", orderProductItemDTO.getProductId());
            Product product = productService.getOne(queryWrapper.last("FOR UPDATE"));
            
            if (product == null) {
                log.error("商品不存在, productId: {}", orderProductItemDTO.getProductId());
                throw new OrderException("商品不存在, productId: " + orderProductItemDTO.getProductId());
            }
            
            // 商品状态检测
            if (product.getStatus() != ProductStatusEnum.SELL) {
                log.error("商品状态异常, productName: {}", product.getName());
                throw new OrderException("商品状态异常, productName: " + product.getName());
            }
            
            // 商品库存检测
            Long productStorage = Optional.ofNullable(product.getStorage()).orElse(0L);
            long newStorage = productStorage - orderProductItemDTO.getQuantity();
            
            if (newStorage < 0) {
                log.error("商品库存不足, productName: {}" ,product.getName());
                throw new OrderException("商品库存不足, productName: " + product.getName());
            }
            
            // 扣减库存
            productService.consume(ProductInOutDTO.builder()
                    .productId(product.getId())
                    .number(newStorage)
                    .build());
            
            // 创建订单商品项
            OrderItem orderItem = OrderItem.builder()
                    .orderId(orderId)
                    .productId(product.getId())
                    .productName(product.getName())
                    .onePrice(product.getSalePrice())
                    .quantity(orderProductItemDTO.getQuantity())
                    .totalAmount(product.getSalePrice() * orderProductItemDTO.getQuantity())
                    .build();
            
            orderItemService.create(orderItem);
            orderItemList.add(orderItem);
            totalAmount += orderItem.getTotalAmount();
        }
        
        // 更新订单信息
        order.setTotalAmount(totalAmount);
        order.setPayType(OrderTypeEnum.PAID);
        order.setPayTime(LocalDateTime.now());
        
        if (!this.updateById(order)) {
            log.error("订单更新失败, orderId: " + orderId);
            throw new OrderException("订单更新失败");
        }
        
        log.info("订单支付成功, orderId: " + orderId);
        response.setData(orderId);
        return response.value();
    }

    /**
     * 用户点击支付创建订单，订单状态为未支付，无需确认商品是否足够，前端已经对商品销售进行限制
     * 只需返回订单ID，即可在确认支付订单页面展示订单的商品信息
     * @return 订单ID
     */
    @Override
    public ResponseEntity<Result<String>> create() {
        Response<String> response = new Response<>();
        String openId = UserThreadUtil.getCurrentOpenId();
        // 创建订单
        String orderId = RandomUtil.generateId();
        Order order = Order.builder()
                .openId(openId)
                .id(orderId)
                .payType(OrderTypeEnum.NONPAYMENT)
                .createTime(LocalDateTime.now())
                .build();
        if(!this.save(order)){
            log.error("订单创建失败");
            throw new OrderException("订单创建失败");
        }
        log.info("订单创建成功, orderId: " + orderId);
        response.setData(orderId);
        return response.value();
    }

    @Override
    public ResponseEntity<Result<String>> cancel(OrderAddDTO orderAddDTO) {
        return null;
    }
    /**
     * 获取订单详细信息
     * @param orderId 订单ID
     * @return 订单商品信息
     */
    @Override
    public ResponseEntity<Result<OrderDetailVO>> detail(Long orderId) {
        Response<OrderDetailVO> response = new Response<>();
        if(orderId==null){
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId));
        if(order==null){
            log.error("订单不存在");
            throw new OrderException("订单不存在");
        }
        List<OrderItem> orderItems = orderItemService.list(orderId);
        OrderDetailVO orderDetailVO = OrderDetailVO.fromOrder(order, orderItems);
        response.setData(orderDetailVO);
        return response.value();
    }


    @Override
    public ResponseEntity<Result<List<OrderInfoVO>>> orderInfoList() {
        Response<List<OrderInfoVO>> response = new Response<>();
        String openId = UserThreadUtil.getCurrentOpenId();
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>().eq("open_id", openId);
        List<Order> orders = this.list(queryWrapper);
        if(orders==null){
            log.info("用户无订单");
            response.setData(new ArrayList<>());
            response.setError("用户无订单");
            return response.value();
        }
        List<OrderInfoVO> orderInfoVOList = orders.stream().map(OrderInfoVO::fromOrder).toList();
        response.setData(orderInfoVOList);
        return response.value();
    }

    @Override
    public ResponseEntity<Result<List<OrderInfoVO>>> orderBossInfoList() {
        return null;
    }

    @Override
    public ResponseEntity<Result<Boolean>> orderOver(String orderId) {
        return null;
    }
}
