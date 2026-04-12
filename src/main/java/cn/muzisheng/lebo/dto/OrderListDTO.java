package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {
    /**
     * 订单类型，1: 未支付，2: 已支付，3: 支付失败 4: 已退款
     */
    private String orderTypeCode;
}
