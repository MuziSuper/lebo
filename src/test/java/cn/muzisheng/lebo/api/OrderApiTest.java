package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.OrderAddDTO;
import cn.muzisheng.lebo.dto.OrderBossListDTO;
import cn.muzisheng.lebo.dto.OrderListDTO;
import cn.muzisheng.lebo.dto.OrderPayDTO;
import cn.muzisheng.lebo.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class OrderApiTest extends UnitTestSupport {

    @Mock
    private OrderService orderService;

    @Test
    void delegatesAllOrderEndpoints() {
        OrderApi api = new OrderApi(orderService);
        OrderAddDTO addDTO = new OrderAddDTO();
        OrderPayDTO payDTO = new OrderPayDTO();
        OrderListDTO listDTO = new OrderListDTO();
        OrderBossListDTO bossListDTO = new OrderBossListDTO();
        api.create(addDTO);
        api.submit(payDTO);
        api.cancel("o1");
        api.orderInfoList(listDTO);
        api.bossOrderInfoList(bossListDTO);
        api.orderOver("o1");
        api.orderReject("o1");
        api.detail("o1");

        verify(orderService).create(addDTO);
        verify(orderService).submit(payDTO);
        verify(orderService).cancel("o1");
        verify(orderService).orderInfoList(listDTO);
        verify(orderService).orderBossInfoList(bossListDTO);
        verify(orderService).orderOver("o1");
        verify(orderService).orderReject("o1");
        verify(orderService).detail("o1");
    }
}
