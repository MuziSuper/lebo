package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单关联单项商品
 */
@Builder
@Entity(name = "order_item")
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    /**
     * 订单关联单项商品ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 订单ID
     */
    @TableField(value = "order_id")
    @Column(name = "order_id")
    private String orderId;
    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    @Column(name = "product_id")
    private String productId;
    /**
     * 商品名称
     */
    @TableField(value = "product_name")
    @Column(name = "product_name")
    private String productName;
    /**
     * 单价
     */
    @TableField(value = "one_price")
    @Column(name = "one_price")
    private Integer onePrice;
    /**
     * 数量
     */
    @Column(name = "quantity")
    @TableField(value = "quantity")
    private Long quantity;
    /**
     * 总金额
     */
    @TableField(value = "total_amount")
    @Column(name = "total_amount")
    private Long totalAmount;
//    /**
//     * 优惠金额
//     */
//    @TableField(value = "discount_amount")
//    @Column(name = "discount_amount")
//    private Integer discountAmount;
//    /**
//     * 实际支付金额
//     */
//    @TableField(value = "pay_amount")
//    @Column(name = "pay_amount")
//    private Long payAmount;
}
