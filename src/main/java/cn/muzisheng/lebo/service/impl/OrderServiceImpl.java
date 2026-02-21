package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.mapper.OrderMapper;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    public ResponseEntity<Result<Long>> create(Long userId) {
        return null;
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
