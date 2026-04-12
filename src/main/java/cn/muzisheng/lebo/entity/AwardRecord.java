package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("award_record")
public class AwardRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Column(name = "create_date")
    @TableField(value = "create_date")
    private LocalDateTime createDate;
    
    @Column(name = "user_openid")
    @TableField(value = "user_openid")
    private String userOpenid;
    
    @Column(name = "award_goods")
    @TableField(value = "award_goods")
    private String awardGoods;
}
