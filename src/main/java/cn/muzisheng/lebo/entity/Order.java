package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.model.OrderOptionEnum;
import cn.muzisheng.lebo.model.OrderTypeEnum;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
@PearObject(
        TableName = "order",
        desc = "订单",
        path = "/order",
        pluralName = "orders"
)
public class Order {
    @Id
    private Long id;
    @Column(name = "merchant_id")
    @TableField(value = "merchant_id")
    private Long merchantId;
    @Column(name = "user_id")
    @TableField(value = "user_id")
    private Long userId;
    @Column(name = "merchant_name")
    @TableField(value = "merchant_name")
    private String merchantName;
    @Column(name = "total_amount")
    @TableField(value = "total_amount")
    private Integer totalAmount;
    @Column(name = "discount_amount")
    @TableField(value = "discount_amount")
    private Integer discountAmount;
    @Column(name = "pay_amount")
    @TableField(value = "pay_amount")
    private Integer payAmount;
    @Column(name = "pay_type")
    @TableField(value = "pay_type")
    private OrderTypeEnum payType;
    @Column(name = "pay_option")
    @TableField(value = "pay_option")
    private OrderOptionEnum payOption;
    @Column(name = "pay_time")
    @TableField(value = "pay_time")
    private LocalDateTime payTime;
    @Column(name = "create_time")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    @Column(name = "end_time")
    @TableField(value = "end_time")
    private LocalDateTime endTime;
    @Column(name = "transaction_id")
    @TableField(value = "transaction_id")
    private String transactionId;

}
