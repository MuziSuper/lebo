package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.Category;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.OrderItem;
import cn.muzisheng.lebo.mapper.CategoryMapper;
import cn.muzisheng.lebo.mapper.OrderItemMapper;
import cn.muzisheng.lebo.mapper.OrderMapper;
import cn.muzisheng.lebo.service.CategoryService;
import cn.muzisheng.lebo.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}
