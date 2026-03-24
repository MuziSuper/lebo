package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户签到记录实体类
 * 用于记录用户的签到状态，包括最近签到日期和连续签到天数
 */
@Entity(name = "user_sign_in")
@TableName("user_sign_in")
@Data
public class UserSignIn {
    /**
     * 签到记录ID，UUID生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 用户openid，唯一标识用户
     */
    @Column(name = "open_id")
    @TableField(value = "open_id")
    private String openId;

    /**
     * 最近一次签到日期
     */
    @Column(name = "last_sign_date")
    @TableField(value = "last_sign_date")
    private LocalDate lastSignDate;

    /**
     * 连续签到天数，断签后重置为1
     */
    @Column(name = "continuous_days")
    @TableField(value = "continuous_days")
    private Integer continuousDays;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT, value = "gmt_created")
    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "gmt_modified")
    @Column(name = "gmt_modified")
    private LocalDateTime gmtModified;
}
