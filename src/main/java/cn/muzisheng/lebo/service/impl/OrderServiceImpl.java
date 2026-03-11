package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderProductItemDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.OrderException;
import cn.muzisheng.lebo.mapper.OrderMapper;
import cn.muzisheng.lebo.model.*;
import cn.muzisheng.lebo.service.OrderService;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.lebo.utils.RandomUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    private final ProductService productService;

    public OrderServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = OrderException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Order>> create(OrderAddDTO orderAddDTO) {
        Response<Order> response = new Response<>();
        String openId = UserThreadUtil.getCurrentOpenId();
        // 参数校验
        if (orderAddDTO == null) {
            log.error("订单信息不能为空");
            throw new OrderException("订单信息不能为空");
        }
        if (orderAddDTO.getCreateTime() == null) {
            log.error("订单创建时间不能为空");
            throw new OrderException("订单创建时间不能为空");
        }
        if (orderAddDTO.getOrderTypeCode() == null) {
            log.error("订单选项不能为空");
            throw new OrderException("订单选项不能为空");
        }
        if (!OrderTypeEnum.contains(orderAddDTO.getOrderTypeCode())) {
            log.error("订单选项不存在");
            throw new OrderException("订单选项不存在");
        }
        // 创建订单
       String orderId = RandomUtil.generateId();
        Order order = Order.builder()
                .openId(orderId)
                .payType(OrderTypeEnum.fromCode(orderAddDTO.getOrderTypeCode()))
                .createTime(orderAddDTO.getCreateTime())
                .build();
        // 订单商品参数判空
        if (orderAddDTO.getOrderProductItemDTOS() == null || orderAddDTO.getOrderProductItemDTOS().isEmpty()) {
            log.error("订单商品不能为空");
            throw new OrderException("订单商品不能为空");
        }
        // 订单商品参数处理
        List<ProductInOutDTO> productInOutDTOS = new ArrayList<>();
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
            if (productStorage<orderProductItemDTO.getQuantity()) {
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
                    .number(orderProductItemDTO.getQuantity())
                    .build();
            productInOutDTOS.add(productInOutDTO);
            totalAmount += orderItem.getTotalAmount();
        }
        order.setTotalAmount(totalAmount);
        for(ProductInOutDTO productInOutDTO:productInOutDTOS){
            productService.consume(productInOutDTO);
        }
        if(!this.save(order)){
            log.error("订单创建失败");
            throw new OrderException("订单创建失败");
        }
        response.setData(order);
        return response.value();
    }


    @Override
    public ResponseEntity<Result<Order>> detail(Long orderId) {
        return null;
    }

    @Override
    public ResponseEntity<Result<List<OrderItem>>> detailItem(Long orderId) {
        return null;
    }

    @Override
    public ResponseEntity<Result<List<Order>>> list(Long userId) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Boolean>> updateStatus(Long orderId) {
        return null;
    }
}
