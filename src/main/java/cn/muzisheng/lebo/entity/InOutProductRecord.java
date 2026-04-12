package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品入库、出库记录
 */
@Data
@Builder
public class InOutProductRecord {
    /**
     * 记录ID
     */
    @Id
    private String id;
    /**
     * 商品名称
     */
    @Column(name = "product_name")
    @TableField("product_name")
    private String productName;
    /**
     * 商品ID
     */
    @Column(name = "product_id")
    @TableField("product_id")
    private String productId;
    /**
     * 出入库描述，可为空
     */
    @Column(name = "`description`")
    @TableField("`description`")
    private String description;
    /**
     * 商品出入库数量，为正数
     */
    @Column(name = "number")
    @TableField("number")
    private Long number;
    /**
     * 商品出入库后剩余数量
     */
    @Column(name = "remain_number")
    @TableField("remain_number")
    private Long remainNumber;
    /**
     * 商品出入库时间
     */
    @Column(name = "time")
    @TableField("time")
    private LocalDateTime time;
    /**
     * 商品出入库类型，1: 入库, 2: 出库
     */
    @Column(name = "type")
    @TableField("type")
    private Integer type;
    /**
     * 商品出入库操作人ID
     */
    @Column(name = "operator_id")
    @TableField("operator_id")
    private String operatorId;


}
