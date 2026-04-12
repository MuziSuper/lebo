package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录
 */
@Entity(name = "point_record")
@Table(name = "point_record")
@Data
@TableName("point_record")
public class PointRecord {
    /**
     * 积分记录ID
     */
    @Column(name = "id")
    @Id
    private String id;
    
    /**
     * 用户openid
     */
    @Column(name = "open_id")
    @TableField(value = "open_id")
    private String openId;
    
    /**
     * 关联订单ID
     */
    @Column(name = "order_id")
    @TableField(value = "order_id")
    private String orderId;
    
    /**
     * 积分变动描述
     */
    @TableField(value = "description")
    @Column(name = "description")
    private String description;
    
    /**
     * 变动积分数量
     */
    @TableField(value = "change_amount")
    @Column(name = "change_amount")
    private Long changeAmount;
    
    /**
     * 变动前积分
     */
    @TableField(value = "before_amount")
    @Column(name = "before_amount")
    private Long beforeAmount;
    
    /**
     * 变动后积分
     */
    @TableField(value = "after_amount")
    @Column(name = "after_amount")
    private Long afterAmount;
    
    /**
     * 创建时间
     */
    @TableField(value = "gmt_created", fill = FieldFill.INSERT)
    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;
}
