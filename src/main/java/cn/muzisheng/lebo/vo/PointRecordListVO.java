package cn.muzisheng.lebo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录列表VO
 */
@Data
public class PointRecordListVO {
    /**
     * 积分记录ID
     */
    private String id;

    /**
     * 关联订单ID
     */
    private String orderId;

    /**
     * 积分变动描述
     */
    private String description;

    /**
     * 变动积分数量
     */
    private Integer changeAmount;

    /**
     * 变动前积分
     */
    private Integer beforeAmount;

    /**
     * 变动后积分
     */
    private Integer afterAmount;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;
}
