package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

public class BaseModel {
    @Column(name = "gmt_created")
    @TableField(value = "gmt_created",fill = FieldFill.INSERT)
    private LocalDateTime gmtCreated;
    @Column(name = "gmt_modified")
    @TableField(value = "gmt_modified",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
