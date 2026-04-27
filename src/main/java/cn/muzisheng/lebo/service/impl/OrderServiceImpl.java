package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.*;
import cn.muzisheng.lebo.entity.*;
import cn.muzisheng.lebo.exception.OrderException;
import cn.muzisheng.lebo.handler.OrderWebSocketHandler;
import cn.muzisheng.lebo.mapper.OrderMapper;
import cn.muzisheng.lebo.model.*;
import cn.muzisheng.lebo.service.*;
import cn.muzisheng.lebo.utils.IdUtil;
import cn.muzisheng.lebo.utils.InformationUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import cn.muzisheng.lebo.vo.OrderDetailVO;
import cn.muzisheng.lebo.vo.OrderInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductService productService;
    private final UserPointService userPointService;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final InformationService informationService;
    
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final ConcurrentHashMap<String, Boolean> pendingOrders = new ConcurrentHashMap<>();

    public OrderServiceImpl(ProductService productService, UserPointService userPointService, OrderItemService orderItemService, UserService userService, InformationService informationService) {
        this.productService = productService;
        this.userPointService = userPointService;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.informationService = informationService;
    }

    /**
     * 用户创建订单，携带选购商品信息
     * 填充订单商品信息、订单状态为未支付、订单创建时间、订单金额、订单用户ID
     * @param orderAddDTO 订单信息（商品列表）
     * @return 订单ID
     */
    @Override
    @Transactional(rollbackFor = OrderException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<String>> create(OrderAddDTO orderAddDTO) {
        Response<String> response = new Response<>();
        
        if (orderAddDTO == null || orderAddDTO.getProductOutDTOList() == null || orderAddDTO.getProductOutDTOList().isEmpty()) {
            log.error("订单商品不能为空");
            throw new OrderException("订单商品不能为空");
        }
        
        String openId = UserThreadUtil.getCurrentOpenId();
        String orderId = IdUtil.generateOrderId();
        // 订单商品列表
        Map<String, OrderItem> orderItemMap = new HashMap<>();
        List<String> productIds = new ArrayList<>();
        // 填充订单商品信息
        for (ProductOutDTO item : orderAddDTO.getProductOutDTOList()) {
            if (item.getProductId() == null || item.getNumber() == null || item.getNumber() <= 0) {
                log.error("订单商品传参异常");
                throw new OrderException("订单商品传参异常");
            }
            productIds.add(item.getProductId());
            orderItemMap.put(item.getProductId(), OrderItem.builder()
                    .orderId(orderId)
                    .productId(item.getProductId())
                    .quantity(item.getNumber())
                    .build());
        }
        // 获取所有商品信息
        List<Product> products = productService.listByIds(productIds);
        if (products.isEmpty()) {
            log.error("商品不存在");
            throw new OrderException("商品不存在");
        }
        if (products.size() != productIds.size()) {
            log.error("部分商品不存在");
            throw new OrderException("部分商品不存在");
        }
        
        List<String> notSellingProducts = new ArrayList<>();
        long totalAmount = 0;
        long totalPoints = 0;
        // 遍历所有商品信息
        for (Product product : products) {
            if (!product.getStatus().equals(ProductStatusEnum.SELL)) {
                notSellingProducts.add(product.getName());
                continue;
            }
            OrderItem orderItem = orderItemMap.get(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setOnePrice(product.getSalePrice());
            orderItem.setTotalAmount(product.getSalePrice() * orderItem.getQuantity());
            totalAmount += orderItem.getTotalAmount();
            totalPoints += product.getPoint() * orderItem.getQuantity();
        }
        
        if (!notSellingProducts.isEmpty()) {
            log.error("商品未在售: {}", notSellingProducts);
            throw new OrderException("商品未在售: " + String.join(", ", notSellingProducts));
        }
        Order order = Order.builder()
                .id(orderId)
                .openId(openId)
                .homeNumber(orderAddDTO.getHomeNumber())
                .totalAmount(totalAmount)
                .pointNumber(totalPoints)
                .payType(OrderTypeEnum.NONPAYMENT)
                .createTime(LocalDateTime.now())
                .build();
        
        if (!this.save(order)) {
            log.error("订单创建失败");
            throw new OrderException("订单创建失败");
        }
        // 批量创建订单项
        orderItemService.createBatch(new ArrayList<>(orderItemMap.values()));
        // 添加订单超时处理任务
        pendingOrders.put(orderId, true);
        // 如果订单超时5分钟未处理，则订单超时处理
        scheduledExecutor.schedule(() -> {
            if (pendingOrders.remove(orderId) != null) {
                handleOrderTimeout(orderId);
            }
        }, 5, TimeUnit.MINUTES);
        
        log.info("订单创建成功, orderId: {}, totalAmount: {}", orderId, totalAmount);
        response.setData(orderId);
        return response.value();
    }

    /**
     * 用户确认支付，传入支付方式
     * 修改订单状态为已支付、填充支付方式、实际支付金额、支付时间
     * @param orderAddDTO 订单信息（订单ID、支付方式）
     * @return 订单ID
     */
    @Override
    @Transactional(rollbackFor = OrderException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<String>> submit(OrderPayDTO orderAddDTO) {
        Response<String> response = new Response<>();

        if (orderAddDTO == null || orderAddDTO.getOrderId() == null) {
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
       String orderId = orderAddDTO.getOrderId();

        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId).last("FOR UPDATE"));
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new OrderException("订单不存在");
        }

        if (order.getPayType() != OrderTypeEnum.NONPAYMENT) {
            log.error("订单状态异常, orderId: {}, payType: {}", orderId, order.getPayType());
            throw new OrderException("订单状态异常");
        }

        if (orderAddDTO.getOrderOptionCode() == null || !OrderOptionEnum.contains(orderAddDTO.getOrderOptionCode())) {
            log.error("支付方式不存在");
            throw new OrderException("支付方式不存在");
        }
        
        order.setPayOption(OrderOptionEnum.fromCode(orderAddDTO.getOrderOptionCode()));
        order.setPayAmount(order.getTotalAmount());
        order.setPayType(OrderTypeEnum.PAID);
        order.setPayTime(LocalDateTime.now());

        if (!this.updateById(order)) {
            log.error("订单支付失败, orderId: {}", orderId);
            throw new OrderException("订单支付失败");
        }
        
        pendingOrders.remove(orderId);

        log.info("订单支付成功, orderId: {}, payAmount: {}", orderId, order.getPayAmount());
        // 通知商户有新订单
        OrderWebSocketHandler.notifyMerchantNewOrder();
        
        response.setData(orderId);
        return response.value();
    }

    /**
     * 处理订单超时
     */
    private void handleOrderTimeout(String orderId) {
        try {
            Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId).last("FOR UPDATE"));
            if (order != null && order.getPayType() == OrderTypeEnum.NONPAYMENT) {
                order.setPayType(OrderTypeEnum.FAILURE);
                order.setPayAmount(0L);
                order.setEndTime(LocalDateTime.now());
                this.updateById(order);
                log.info("订单超时自动取消, orderId: {}", orderId);
            }
        } catch (Exception e) {
            log.error("处理订单超时异常, orderId: {}, error: {}", orderId, e.getMessage());
        }
    }

    /**
     * 用户取消支付，修改订单状态为支付失败，填充订单结束时间、实际支付金额为0
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    @Override
    public ResponseEntity<Result<Boolean>> cancel(String orderId) {
        Response<Boolean> response = new Response<>();

        if (orderId == null || orderId.trim().isEmpty()) {
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }

        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId));
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new OrderException("订单不存在");
        }

        if (order.getPayType() != OrderTypeEnum.NONPAYMENT) {
            log.error("订单状态异常，无法取消, orderId: {}, payType: {}", orderId, order.getPayType());
            throw new OrderException("订单状态异常，无法取消");
        }

        order.setPayType(OrderTypeEnum.FAILURE);
        order.setEndTime(LocalDateTime.now());
        order.setPayAmount(0L);

        if (!this.updateById(order)) {
            log.error("订单取消失败, orderId: {}", orderId);
            throw new OrderException("订单取消失败");
        }
        
        pendingOrders.remove(orderId);

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
    public ResponseEntity<Result<OrderDetailVO>> detail(String orderId) {
        Response<OrderDetailVO> response = new Response<>();
        if (orderId == null || orderId.trim().isEmpty()) {
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId));
        if (order == null) {
            log.error("订单不存在");
            throw new OrderException("订单不存在");
        }
        List<OrderItem> orderItems = orderItemService.listByOrderId(orderId);

        List<String> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();
        Map<String, String> productImageMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<Product> products = productService.listByIds(productIds);
            productImageMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, Product::getImage, (a, b) -> a));
        }

        User user = userService.getUserByOpenId(order.getOpenId());
        String nickName = user != null ? user.getNickName() : null;
        OrderDetailVO orderDetailVO = OrderDetailVO.fromOrder(order, orderItems, nickName, productImageMap);
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
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>().eq("open_id", openId).orderByDesc("create_time");
        
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
        
        User user = userService.getUserByOpenId(openId);
        String nickName = user != null ? user.getNickName() : null;
        List<OrderInfoVO> orderInfoVOList = orders.stream()
                .map(order -> OrderInfoVO.fromOrder(order, nickName))
                .toList();
        response.setData(orderInfoVOList);
        return response.value();
    }
    /**
     * 商家获取订单列表，筛选条件为订单状态,订单创建时间区间，订单结束时间区间，订单支付方式，订单ID
     * @param orderBossListDTO 订单列表查询条件
     * @return 订单分页数据
     */
    @Override
    public ResponseEntity<Result<IPage<OrderInfoVO>>> orderBossInfoList(OrderBossListDTO orderBossListDTO) {
        Response<IPage<OrderInfoVO>> response = new Response<>();
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (orderBossListDTO != null) {
            if (orderBossListDTO.getOrderId() != null && !orderBossListDTO.getOrderId().trim().isEmpty()) {
                queryWrapper.like("id", orderBossListDTO.getOrderId());
            }
            
            // 订单号筛选
            if (orderBossListDTO.getOpenId() != null && !orderBossListDTO.getOpenId().trim().isEmpty()) {
                queryWrapper.eq("open_id", orderBossListDTO.getOpenId());
            }
            
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
        
        int pageNum = Optional.ofNullable(orderBossListDTO)
                .map(OrderBossListDTO::getPageNum)
                .filter(num -> num > 0)
                .orElse(1);
        int pageSize = Optional.ofNullable(orderBossListDTO)
                .map(OrderBossListDTO::getPageSize)
                .filter(size -> size > 0)
                .orElse(9);

        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> orderPage = this.page(page, queryWrapper);

        IPage<OrderInfoVO> voPage = orderPage.convert(order -> {
            User user = userService.getUserByOpenId(order.getOpenId());
            String nickName = user != null ? user.getNickName() : null;
            return OrderInfoVO.fromOrder(order, nickName);
        });

        response.setData(voPage);
        
        return response.value();
    }
    /**
     * 商家确认接单结束，对订单内的商品进行出库操作以及记录日志、用户积分钱包加积分并记录日志
     * 订单填充实际支付金额、结束时间，更新订单状态为已结束
     * @param orderId 订单Id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> orderOver(String orderId) {
        Response<Boolean> response = new Response<>();
        
        if (orderId == null || orderId.trim().isEmpty()) {
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId).last("FOR UPDATE"));
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new OrderException("订单不存在");
        }
        
        if (order.getPayType() != OrderTypeEnum.PAID) {
            log.error("订单状态异常，无法结束, orderId: {}, payType: {}", orderId, order.getPayType());
            throw new OrderException("订单状态异常，只有已支付的订单才能结束");
        }
        
        List<OrderItem> orderItems = orderItemService.listByOrderId(orderId);
        
        List<ProductInOutDTO> inOutDTOList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            inOutDTOList.add(ProductInOutDTO.builder()
                    .productId(orderItem.getProductId())
                    .number(-orderItem.getQuantity())
                    .description("订单商品出库")
                    .build());
        }
        
        productService.inOutBatch(inOutDTOList);
        log.info("订单商品出库成功, orderId: {}", orderId);
        
        List<String> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();
        
        List<Product> products = productService.listByIds(productIds);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        
        long totalPoint = 0L;
        for (OrderItem orderItem : orderItems) {
            Product product = productMap.get(orderItem.getProductId());
            if (product != null && product.getPoint() != null) {
                totalPoint += product.getPoint() * orderItem.getQuantity();
            }
        }
        
        order.setPayType(OrderTypeEnum.OVER);
        order.setPayAmount(order.getTotalAmount());
        order.setPointNumber(totalPoint);
        order.setEndTime(LocalDateTime.now());
        
        if (!this.updateById(order)) {
            log.error("订单结束失败, orderId: {}", orderId);
            throw new OrderException("订单结束失败");
        }
        
        if (totalPoint > 0) {
            try {
                userPointService.updatePoint(order.getOpenId(), totalPoint,PointRecordTypeEnum.ORDER_PAY);
                log.info("订单积分已计入用户钱包, orderId: {}, openId: {}, totalPoint: {}", orderId, order.getOpenId(), totalPoint);
            } catch (Exception e) {
                log.error("订单积分计入用户钱包失败, orderId: {}, openId: {}, totalPoint: {}, error: {}", orderId, order.getOpenId(), totalPoint, e.getMessage());
                throw new OrderException("订单积分计入用户钱包失败");
            }
        }
        
        log.info("订单结束成功, orderId: {}, payAmount: {}, totalPoint: {}", orderId, order.getPayAmount(), totalPoint);
        
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("您的订单已确认完成！\n");
        contentBuilder.append("订单号：").append(orderId).append("\n");
        contentBuilder.append("订单金额：").append(order.getTotalAmount()).append("元\n");
        if (totalPoint > 0) {
            contentBuilder.append("获得积分：").append(totalPoint);
        }
        
        Information orderOverInfo = InformationUtil.buildPersonalNotification(
                order.getOpenId(),
                "订单确认成功",
                contentBuilder.toString()
        );
        informationService.save(orderOverInfo);
        log.info("订单确认成功消息已发送, orderId: {}, openId: {}", orderId, order.getOpenId());
        
        OrderWebSocketHandler.notifyCustomerOrderAccepted(order.getOpenId());
        
        response.setData(true);
        return response.value();
    }

    /**
     * 商家拒绝接单，修改订单状态为已退款，最终支付价格为0，商品库存退回，返回是否成功
     * @param orderId 订单Id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = OrderException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> orderReject(String orderId) {
        Response<Boolean> response = new Response<>();
        
        if (orderId == null || orderId.trim().isEmpty()) {
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId).last("FOR UPDATE"));
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new OrderException("订单不存在");
        }
        
        if (order.getPayType() != OrderTypeEnum.PAID) {
            log.error("订单状态异常，无法拒绝, orderId: {}, payType: {}", orderId, order.getPayType());
            throw new OrderException("订单状态异常，只有已支付的订单才能拒绝");
        }
        
        order.setPayType(OrderTypeEnum.REFUNDED);
        order.setPayAmount(0L);
        order.setEndTime(LocalDateTime.now());
        
        if (!this.updateById(order)) {
            log.error("订单拒绝失败, orderId: {}", orderId);
            throw new OrderException("订单拒绝失败");
        }
        
        log.info("订单拒绝成功，商品库存已退回, orderId: {}", orderId);
        
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("很抱歉，您的订单已被商家拒绝。\n");
        contentBuilder.append("订单号：").append(orderId).append("\n");
        contentBuilder.append("订单金额将原路退回。");
        
        Information orderRejectInfo = InformationUtil.buildPersonalNotification(
                order.getOpenId(),
                "订单已被拒绝",
                contentBuilder.toString()
        );
        informationService.save(orderRejectInfo);
        log.info("订单拒绝消息已发送, orderId: {}, openId: {}", orderId, order.getOpenId());
        
        OrderWebSocketHandler.notifyCustomerOrderRejected(order.getOpenId());
        
        response.setData(true);
        return response.value();
    }
}
