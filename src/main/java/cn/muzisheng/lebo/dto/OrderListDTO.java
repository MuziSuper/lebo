package cn.muzisheng.lebo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderListDTO {
    /**
     * 订单类型,1: 未支付,2: 已支付,3: 支付失败 4: 已退款
     */
    private String orderTypeCode;
}
