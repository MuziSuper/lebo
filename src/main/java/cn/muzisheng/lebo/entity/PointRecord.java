package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 积分记录
 */
@Entity(name = "point_record")
@Table(name = "point_record")
public class PointRecord {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "open_id")
    @TableField(value = "open_id")
    private String openId;
    @Column(name = "order_id")
    @TableField(value = "order_id")
    private String orderId;
    private String description;
    @TableField(value = "gmt_created")
    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;
    @TableField(value = "change_amount")
    @Column(name = "change_amount")
    private Integer changeAmount;
    @TableField(value = "before_amount")
    @Column(name = "before_amount")
    private Integer beforeAmount;
    @TableField(value = "after_amount")
    @Column(name = "after_amount")
    private Integer afterAmount;
}
