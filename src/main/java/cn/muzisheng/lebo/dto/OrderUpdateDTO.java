package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDTO {
    private String id;
    /**
     * 实际支付金额
     */
    private Long payAmount;
    /**
     * 支付方式,1: 未支付, 2: 已支付,3: 支付失败, 4: 已退款
     */
    private Integer payType;
    /**
     * 支付状态,0: 线下支付, 1: 微信支付, 2: 支付宝支付
     */
    private Integer payOption;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
