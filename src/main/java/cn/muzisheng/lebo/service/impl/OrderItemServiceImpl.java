package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.exception.OrderException;
import cn.muzisheng.lebo.mapper.OrderItemMapper;
import cn.muzisheng.lebo.service.OrderItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    @Override
    public boolean create(OrderItem orderItem) throws OrderException{
        if(!save(orderItem)){
            log.error("订单项创建失败, orderId:{}, productId:{}",orderItem.getOrderId(),orderItem.getProductId());
            throw new OrderException("订单项创建失败, orderId: "+orderItem.getOrderId()+", productId: "+orderItem.getProductId());
        }
        return true;
    }

    @Override
    public void createBatch(List<OrderItem> orderItems) throws OrderException {
        if (orderItems == null || orderItems.isEmpty()) {
            log.error("订单项列表不能为空");
            throw new OrderException("订单项列表不能为空");
        }
        if (!saveBatch(orderItems)) {
            log.error("订单项批量创建失败");
            throw new OrderException("订单项批量创建失败");
        }
    }

    @Override
    public List<OrderItem> list(Long orderId) {
        if(orderId == null){
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        List<OrderItem> orderItems =  list(new QueryWrapper<OrderItem>().eq("order_id", orderId));
        if(orderItems == null|| orderItems.isEmpty()){
            log.error("订单项列表为空, orderId:{}",orderId);
            throw new OrderException("订单项列表为空, orderId: "+orderId);
        }
        return orderItems;
    }

    @Override
    public List<OrderItem> listByOrderId(String orderId) {
        if(orderId == null || orderId.trim().isEmpty()){
            log.error("订单ID不能为空");
            throw new OrderException("订单ID不能为空");
        }
        List<OrderItem> orderItems =  list(new QueryWrapper<OrderItem>().eq("order_id", orderId));
        if(orderItems == null|| orderItems.isEmpty()){
            log.error("订单项列表为空, orderId:{}",orderId);
            throw new OrderException("订单项列表为空, orderId: "+orderId);
        }
        return orderItems;
    }
}
