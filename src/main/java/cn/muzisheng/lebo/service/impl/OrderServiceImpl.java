package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderBossListDTO;
import cn.muzisheng.lebo.dto.OrderListDTO;
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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

        // 检查订单是否超时
        checkOrderTimeout(order);

        // 校验订单商品
        if (orderAddDTO.getProductInOutDTOList() == null || orderAddDTO.getProductInOutDTOList().isEmpty()) {
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

        // 准备批量出库DTO列表和订单项映射
        List<ProductInOutDTO> inOutDTOList = new ArrayList<>();
        Map<String, OrderItem> orderItemMap = new HashMap<>();

        for (ProductInOutDTO orderProductItemDTO : orderAddDTO.getProductInOutDTOList()) {
            // 商品参数校验
            if (orderProductItemDTO.getProductId() == null ||
                orderProductItemDTO.getNumber() == null ||
                orderProductItemDTO.getNumber() <= 0) {
                log.error("订单商品传参异常");
                throw new OrderException("订单商品传参异常");
            }

            // 准备批量出库数据（负数表示出库）
            inOutDTOList.add(ProductInOutDTO.builder()
                    .productId(orderProductItemDTO.getProductId())
                    .number(-orderProductItemDTO.getNumber())  // 负数表示出库
                    .build());

            // 创建订单商品项（暂时只设置基本信息，价格信息在获取商品后填充）
            OrderItem orderItem = OrderItem.builder()
                    .orderId(orderId)
                    .productId(orderProductItemDTO.getProductId())
                    .quantity(orderProductItemDTO.getNumber())
                    .build();

            orderItemMap.put(orderProductItemDTO.getProductId(), orderItem);
        }

        // 批量扣减库存并获取商品信息
        List<Product> productList = productService.inOutBatch(inOutDTOList);

        // 填充订单项的商品信息和计算总金额
        long totalAmount = 0;
        for (Product product : productList) {
            OrderItem orderItem = orderItemMap.get(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setOnePrice(product.getSalePrice());
            orderItem.setTotalAmount(product.getSalePrice() * orderItem.getQuantity());
            totalAmount += orderItem.getTotalAmount();
        }

        // 批量创建订单项
        orderItemService.createBatch(new ArrayList<>(orderItemMap.values()));

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

    /**
     * 用户在订单确认支付页面点击了取消，则修改订单状态为支付失败，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单信息
     * @return 是否取消成功
     */
    @Override
    public ResponseEntity<Result<Boolean>> cancel(Long orderId) {
        Response<Boolean> response = new Response<>();

        // 参数校验
        if (orderId == null) {
            log.error("订单Id不能为空");
            throw new OrderException("订单Id不能为空");
        }


        // 查询订单
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId));
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new OrderException("订单不存在");
        }

        // 验证订单状态，只有未支付的订单才能取消
        if (order.getPayType() != OrderTypeEnum.NONPAYMENT) {
            log.error("订单状态异常，无法取消, orderId: {}, payType: {}", orderId, order.getPayType());
            throw new OrderException("订单状态异常，无法取消");
        }

        // 检查订单是否超时
        checkOrderTimeout(order);

        // 更新订单状态为支付失败
        order.setPayType(OrderTypeEnum.FAILURE);
        order.setEndTime(LocalDateTime.now());
        order.setPayAmount(0L);

        if (!this.updateById(order)) {
            log.error("订单取消失败, orderId: {}", orderId);
            throw new OrderException("订单取消失败");
        }

        log.info("订单取消成功, orderId: {}", orderId);
        response.setData(true);
        return response.value();
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
    /**
     * 用户获取订单列表，筛选条件为订单状态
     * @param orderListDTO 订单列表
     * @return 订单详情
     */
    @Override
    public ResponseEntity<Result<List<OrderInfoVO>>> orderInfoList(OrderListDTO orderListDTO) {
        Response<List<OrderInfoVO>> response = new Response<>();
        String openId = UserThreadUtil.getCurrentOpenId();
        
        // 构建查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>().eq("open_id", openId);
        
        // 如果指定了订单状态，添加状态筛选条件
        if (orderListDTO != null && orderListDTO.getOrderTypeCode() != null) {
            try {
                Integer statusCode = Integer.parseInt(orderListDTO.getOrderTypeCode());
                if (OrderTypeEnum.contains(statusCode)) {
                    queryWrapper.eq("pay_type", statusCode);
                } else {
                    log.error("订单状态不存在, statusCode: {}", statusCode);
                    throw new OrderException("订单状态不存在, statusCode: " + statusCode);
                }
            } catch (NumberFormatException e) {
                log.error("订单状态格式错误, orderTypeCode: {}", orderListDTO.getOrderTypeCode());
                throw new OrderException("订单状态格式错误");
            }
        }
        
        List<Order> orders = this.list(queryWrapper);
        if (orders == null || orders.isEmpty()) {
            log.info("用户无订单, openId: {}", openId);
            response.setData(new ArrayList<>());
            return response.value();
        }
        
        List<OrderInfoVO> orderInfoVOList = orders.stream().map(OrderInfoVO::fromOrder).toList();
        response.setData(orderInfoVOList);
        return response.value();
    }
    /**
     * 商家获取订单列表，筛选条件为订单状态,订单创建时间区间，订单结束时间区间，订单支付方式，订单ID
     * @param orderBossListDTO 订单列表
     * @return 订单详情
     */
    @Override
    public ResponseEntity<Result<List<OrderInfoVO>>> orderBossInfoList(OrderBossListDTO orderBossListDTO) {
        Response<List<OrderInfoVO>> response = new Response<>();
        
        // 构建查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (orderBossListDTO != null) {
            // 订单号筛选
            if (orderBossListDTO.getOrderId() != null && !orderBossListDTO.getOrderId().trim().isEmpty()) {
                queryWrapper.like("id", orderBossListDTO.getOrderId());
            }
            
            // 订单状态筛选
            if (orderBossListDTO.getOrderTypeCode() != null && !orderBossListDTO.getOrderTypeCode().trim().isEmpty()) {
                try {
                    Integer statusCode = Integer.parseInt(orderBossListDTO.getOrderTypeCode());
                    if (OrderTypeEnum.contains(statusCode)) {
                        queryWrapper.eq("pay_type", statusCode);
                    } else {
                        log.error("订单状态不存在, statusCode: {}", statusCode);
                        throw new OrderException("订单状态不存在, statusCode: " + statusCode);
                    }
                } catch (NumberFormatException e) {
                    log.error("订单状态格式错误, orderTypeCode: {}", orderBossListDTO.getOrderTypeCode());
                    throw new OrderException("订单状态格式错误");
                }
            }
            
            // 支付方式筛选
            if (orderBossListDTO.getOrderOptionCode() != null && !orderBossListDTO.getOrderOptionCode().trim().isEmpty()) {
                try {
                    Integer optionCode = Integer.parseInt(orderBossListDTO.getOrderOptionCode());
                    if (OrderOptionEnum.contains(optionCode)) {
                        queryWrapper.eq("pay_option", optionCode);
                    } else {
                        log.error("支付方式不存在, optionCode: {}", optionCode);
                        throw new OrderException("支付方式不存在, optionCode: " + optionCode);
                    }
                } catch (NumberFormatException e) {
                    log.error("支付方式格式错误, orderOptionCode: {}", orderBossListDTO.getOrderOptionCode());
                    throw new OrderException("支付方式格式错误");
                }
            }
            
            // 订单创建时间区间筛选
            if (orderBossListDTO.getOrderCreateTime() != null && !orderBossListDTO.getOrderCreateTime().trim().isEmpty()) {
                String[] createTimeRange = orderBossListDTO.getOrderCreateTime().split(",");
                if (createTimeRange.length == 2) {
                    try {
                        LocalDateTime startTime = LocalDateTime.parse(createTimeRange[0].trim());
                        LocalDateTime endTime = LocalDateTime.parse(createTimeRange[1].trim());
                        queryWrapper.between("create_time", startTime, endTime);
                    } catch (Exception e) {
                        log.error("订单创建时间格式错误, orderCreateTime: {}", orderBossListDTO.getOrderCreateTime());
                        throw new OrderException("订单创建时间格式错误，请使用格式: yyyy-MM-ddTHH:mm:ss,yyyy-MM-ddTHH:mm:ss");
                    }
                }
            }
            
            // 订单结束时间区间筛选
            if (orderBossListDTO.getOrderEndTime() != null && !orderBossListDTO.getOrderEndTime().trim().isEmpty()) {
                String[] endTimeRange = orderBossListDTO.getOrderEndTime().split(",");
                if (endTimeRange.length == 2) {
                    try {
                        LocalDateTime startTime = LocalDateTime.parse(endTimeRange[0].trim());
                        LocalDateTime endTime = LocalDateTime.parse(endTimeRange[1].trim());
                        queryWrapper.between("end_time", startTime, endTime);
                    } catch (Exception e) {
                        log.error("订单结束时间格式错误, orderEndTime: {}", orderBossListDTO.getOrderEndTime());
                        throw new OrderException("订单结束时间格式错误，请使用格式: yyyy-MM-ddTHH:mm:ss,yyyy-MM-ddTHH:mm:ss");
                    }
                }
            }
        }
        
        List<Order> orders = this.list(queryWrapper);
        if (orders == null || orders.isEmpty()) {
            log.info("无符合条件的订单");
            response.setData(new ArrayList<>());
            return response.value();
        }
        
        List<OrderInfoVO> orderInfoVOList = orders.stream().map(OrderInfoVO::fromOrder).toList();
        response.setData(orderInfoVOList);
        return response.value();
    }
    /**
     * 商家确认订单结束，修改订单状态为已结束，完善订单结束时间以及最终支付价格，返回是否成功
     * @param orderId 订单Id
     * @return 订单ID
     */
    @Override
    public ResponseEntity<Result<Boolean>> orderOver(String orderId) {
        Response<Boolean> response = new Response<>();
        
        // 参数校验
        if (orderId == null || orderId.trim().isEmpty()) {
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        
        // 查询订单
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId));
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new OrderException("订单不存在");
        }
        
        // 验证订单状态，只有已支付的订单才能结束
        if (order.getPayType() != OrderTypeEnum.PAID) {
            log.error("订单状态异常，无法结束, orderId: {}, payType: {}", orderId, order.getPayType());
            throw new OrderException("订单状态异常，只有已支付的订单才能结束");
        }
        
        // 更新订单状态为已结束
        order.setPayType(OrderTypeEnum.OVER);
        order.setEndTime(LocalDateTime.now());
        
        if (!this.updateById(order)) {
            log.error("订单结束失败, orderId: {}", orderId);
            throw new OrderException("订单结束失败");
        }
        
        log.info("订单结束成功, orderId: {}", orderId);
        response.setData(true);
        return response.value();
    }

    /**
     * 检查订单是否超时（创建时间超过5分钟）
     * 如果超时，则将订单状态更新为支付失败并抛出异常
     * @param order 订单对象
     * @throws OrderException 订单异常
     */
    private void checkOrderTimeout(Order order) throws OrderException {
        if (order.getCreateTime() != null &&
                order.getCreateTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            // 更新订单状态为支付失败
            order.setPayType(OrderTypeEnum.FAILURE);
            order.setPayAmount(0L);
            order.setEndTime(LocalDateTime.now());
            this.updateById(order);
            log.error("订单超时, orderId: {}", order.getId());
            throw new OrderException("订单超时，请重新下单");
        }
    }
}
