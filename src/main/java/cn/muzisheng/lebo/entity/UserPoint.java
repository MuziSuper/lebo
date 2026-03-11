package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity(name = "user_point")
@TableName("user_point")
@Data
public class UserPoint {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 用户的openid
     */
    @Column(name = "open_id")
    @TableField(value = "open_id")
    private String openId;
    /**
     * 当前积分
     */
    @Column(name = "current_point")
    @TableField(value = "current_point")
    private Long currentPoint;
    /**
     * 累计积分
     */
    @TableField(value = "accumulated_point")
    @Column(name = "accumulated_point")
    private Long accumulatedPoint;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "gmt_modified")
    @Column(name = "gmt_modified")
    private LocalDateTime gmtModified;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "gmt_created")
    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;
    /**
     * 逻辑删除
     */
    @Column(name = "is_deleted")
    @TableField(value = "is_deleted")
    private Integer deleted;
}
