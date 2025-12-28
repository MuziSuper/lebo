package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;

@Entity(name = "order_item")
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @TableField(value = "order_id")
    @Column(name = "order_id")
    private Long orderId;
    @TableField(value = "product_id")
    @Column(name = "product_id")
    private Long productId;
    @TableField(value = "product_name")
    @Column(name = "product_name")
    private String productName;
    @TableField(value = "one_price")
    @Column(name = "one_price")
    private Integer onePrice;
    private Integer quantity;
    @TableField(value = "total_amount")
    @Column(name = "total_amount")
    private Integer totalAmount;
    @TableField(value = "discount_amount")
    @Column(name = "discount_amount")
    private Integer discountAmount;
    @TableField(value = "pay_amount")
    @Column(name = "pay_amount")
    private Integer payAmount;
}
