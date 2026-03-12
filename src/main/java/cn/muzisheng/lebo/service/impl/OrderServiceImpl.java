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

    @Override
    @Transactional(rollbackFor = OrderException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<String>> create(OrderAddDTO orderAddDTO) {
        Response<String> response = new Response<>();
        String openId = UserThreadUtil.getCurrentOpenId();
        // 参数校验
        if (orderAddDTO == null) {
            log.error("订单信息不能为空");
            throw new OrderException("订单信息不能为空");
        }
        if (orderAddDTO.getOrderOptionCode() == null) {
            log.error("订单选项不能为空");
            throw new OrderException("订单选项不能为空");
        }
        if (!OrderOptionEnum.contains(orderAddDTO.getOrderOptionCode())) {
            log.error("订单选项不存在");
            throw new OrderException("订单选项不存在");
        }
        // 创建订单
       String orderId = RandomUtil.generateId();
        Order order = Order.builder()
                .openId(openId)
                .id(orderId)
                .payType(OrderTypeEnum.NONPAYMENT)
                .payOption(OrderOptionEnum.fromCode(orderAddDTO.getOrderOptionCode()))
                .createTime(LocalDateTime.now())
                .build();
        // 订单商品参数判空
        if (orderAddDTO.getOrderProductItemDTOS() == null || orderAddDTO.getOrderProductItemDTOS().isEmpty()) {
            log.error("订单商品不能为空");
            throw new OrderException("订单商品不能为空");
        }
        // 订单商品参数处理
        List<ProductInOutDTO> productInOutDTOS = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();
        long totalAmount = 0;
        for (OrderProductItemDTO orderProductItemDTO : orderAddDTO.getOrderProductItemDTOS()) {
            // 订单商品参数校验
            if (orderProductItemDTO.getProductId() == null || orderProductItemDTO.getQuantity() == null || orderProductItemDTO.getQuantity() <= 0) {
                log.error("订单商品传参异常");
                throw new OrderException("订单商品传参异常");
            }
            // 查询商品是否库存足够，使用for update乐观锁，锁住库存
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", orderProductItemDTO.getProductId());
            Product product = productService.getOne(queryWrapper.last("FOR UPDATE"));
            // 商品判空
            if (product == null) {
                log.error("商品不存在");
                throw new OrderException("商品不存在");
            }
            // 商品状态检测
            if (product.getStatus() != ProductStatusEnum.SELL) {
                log.error("商品状态异常, name: " + product.getName());
                throw new OrderException("商品状态异常");
            }
            // 商品库存检测
            Long productStorage= Optional.ofNullable(product.getStorage()).orElse(0L);
            if(orderProductItemDTO.getQuantity()==null){
                log.error("商品数量不能为空");
                throw new OrderException("商品数量不能为空");
            }
            long newStorage = productStorage - orderProductItemDTO.getQuantity();
            if (newStorage<0) {
                log.error("商品库存不足, name: " + product.getName());
                throw new OrderException("商品库存不足");
            }
            // 填充订单商品实体类
            OrderItem orderItem = OrderItem.builder()
                    .orderId(orderId)
                    .productId(product.getId())
                    .productName(product.getName())
                    .onePrice(product.getSalePrice())
                    .quantity(orderProductItemDTO.getQuantity())
                    .totalAmount(product.getSalePrice() * orderProductItemDTO.getQuantity())
                    .build();
            // 减库存,放入列表中，离开循环后统一处理
            ProductInOutDTO productInOutDTO = ProductInOutDTO.builder()
                    .productId(product.getId())
                    .number(newStorage)
                    .build();
            productInOutDTOS.add(productInOutDTO);
            orderItemList.add(orderItem);
            totalAmount += orderItem.getTotalAmount();
        }
        order.setTotalAmount(totalAmount);
        for(ProductInOutDTO productInOutDTO:productInOutDTOS){
            productService.consume(productInOutDTO);
        }
        for(OrderItem orderItem:orderItemList){
            orderItemService.create(orderItem);
        }
        if(!this.save(order)){
            log.error("订单创建失败");
            throw new OrderException("订单创建失败");
        }
        log.info("订单创建成功, orderId: " + orderId);
        response.setData(openId);
        return response.value();
    }


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
    public ResponseEntity<Result<Boolean>> updateOrder(OrderUpdateDTO orderUpdateDTO){
        Response<Boolean> response = new Response<>();
        if(orderUpdateDTO==null){
            log.error("订单参数不能为空");
            throw new OrderException("订单参数不能为空");
        }
        String orderId=orderUpdateDTO.getId();
        if(orderId==null){
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        Order order = this.getOne(new QueryWrapper<Order>().eq("id", orderId));
        if(order==null){
            log.error("订单不存在");
            throw new OrderException("订单不存在");
        }
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", orderId);
        if(orderUpdateDTO.getPayAmount()!=null){
            updateWrapper.set("pay_amount", orderUpdateDTO.getPayAmount());
        }

        if(orderUpdateDTO.getPayOption()!=null){
            updateWrapper.set("pay_option", orderUpdateDTO.getPayOption());
        }
        if(orderUpdateDTO.getPayType()!=null){
            updateWrapper.set("pay_type", orderUpdateDTO.getPayType());
            if(orderUpdateDTO.getPayType()==2){
                if(orderUpdateDTO.getPayTime()!=null){
                    updateWrapper.set("pay_time", orderUpdateDTO.getPayTime());
                }else{
                    updateWrapper.set("pay_time", LocalDateTime.now());
                }
                if(orderUpdateDTO.getEndTime()!=null)
            }
        }
        if(orderUpdateDTO.getEndTime()!=null){
            updateWrapper.set("end_time", orderUpdateDTO.getEndTime());
        }

        updateWrapper.set("pay_type", orderUpdateDTO.getPayType());

    }
}
