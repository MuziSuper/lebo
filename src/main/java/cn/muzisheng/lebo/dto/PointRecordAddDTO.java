package cn.muzisheng.lebo.dto;

import lombok.Data;

/**
 * 新增积分记录DTO
 */
@Data
public class PointRecordAddDTO {
    /**
     * 用户openid
     */
    private String openId;

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
    private Long changeAmount;

    /**
     * 变动前积分
     */
    private Long beforeAmount;

    /**
     * 变动后积分
     */
    private Long afterAmount;
}
