package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity(name = "user_point")
@TableName("user_point")
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    @TableField(value = "user_id")
    private String userId;
    @Column(name = "total_point")
    @TableField(value = "total_point")
    private Long totalPoint;
    @Column(name = "accumulated_point")
    private Long accumulatedPoint;
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "gmt_modified")
    @Column(name = "gmt_modified")
    private LocalDateTime gmtModified;
}
