package cn.muzisheng.lebo.dto;

import lombok.Data;

@Data
public class OrderBossListDTO {
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 订单状态,1: 未支付,2: 已支付,3: 支付失败 4: 已退款
     */
    private String orderTypeCode;
    /**
     * 订单类型，1: 线下支付，2: 微信支付，3: 支付宝支付
     */
    private String orderOptionCode;
    /**
     * 订单创建时间区间，格式: startTime,endTime
     */
    private String orderCreateTime;
    /**
     * 订单结束时间区间，格式: startTime,endTime
     */
    private String orderEndTime;
    /**
     * 用户openId
     */
    private String openId;
    /**
     * 页码，默认为null表示不分页
     */
    private Integer pageNum;
    /**
     * 每页数量，默认为null表示不分页
     */
    private Integer pageSize;
}
