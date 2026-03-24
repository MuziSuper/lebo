package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.model.OrderOptionEnum;
import cn.muzisheng.lebo.model.OrderTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * 订单表
 */
@Builder
@Data
@TableName("`order`")
public class Order {
    @Id
    private String id;
    /**
     * 用户ID
     */
    @Column(name = "open_id")
    @TableField(value = "open_id")
    private String openId;
    /**
     * 房间号
     */
    @Column(name = "home_number")
    @TableField(value = "home_number")
    private String homeNumber;
    /**
     * 订单金额
     */
    @Column(name = "total_amount")
    @TableField(value = "total_amount")
    private Long totalAmount;
//    /**
//     * 优惠金额
//     */
//    @Column(name = "discount_amount")
//    @TableField(value = "discount_amount")
//    private Long discountAmount;
    /**
     * 实际支付金额
     */
    @Column(name = "pay_amount")
    @TableField(value = "pay_amount")
    private Long payAmount;
    /**
     * 支付状态,1: 未支付, 2: 已支付,3: 支付失败, 4: 已退款，5: 已结束
     */
    @Column(name = "pay_type")
    @TableField(value = "pay_type")
    private OrderTypeEnum payType;
    /**
     * 支付方式
     */
    @Column(name = "pay_option")
    @TableField(value = "pay_option")
    private OrderOptionEnum payOption;
    /**
     * 支付时间
     */
    @Column(name = "pay_time")
    @TableField(value = "pay_time")
    private LocalDateTime payTime;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @TableField(value = "end_time")
    private LocalDateTime endTime;

}
