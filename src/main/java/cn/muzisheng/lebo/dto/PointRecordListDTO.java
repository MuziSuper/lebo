package cn.muzisheng.lebo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录查询DTO
 */
@Data
public class PointRecordListDTO {
    /**
     * 积分记录ID
     */
    private String id;

    /**
     * 用户openid
     */
    private String openId;

    /**
     * 关联订单ID
     */
    private String orderId;

    /**
     * 开始创建时间
     */
    private LocalDateTime startTime;

    /**
     * 结束创建时间
     */
    private LocalDateTime endTime;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;
}
