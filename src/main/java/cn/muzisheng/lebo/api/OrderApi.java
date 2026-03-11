package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderApi {
    private final OrderService orderService;
    public OrderApi(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/create")
    public ResponseEntity<Result<Order>> create(OrderAddDTO orderAddDTO) {
        return orderService.create(orderAddDTO);
    }

}
