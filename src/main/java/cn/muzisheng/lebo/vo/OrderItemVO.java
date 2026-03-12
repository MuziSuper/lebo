package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.OrderItem;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
        /**
         * 商品ID
         */
        private String productId;
        /**
         * 商品名称
         */
        private String productName;
        /**
         * 单价
         */
        private Integer onePrice;
        /**
         * 数量
         */
        private Long quantity;
        /**
         * 总金额
         */
        private Long totalAmount;
        public static OrderItemVO fromOrderItem(OrderItem orderItem) {
                return OrderItemVO.builder()
                        .productId(orderItem.getProductId())
                        .productName(orderItem.getProductName())
                        .onePrice(orderItem.getOnePrice())
                        .quantity(orderItem.getQuantity())
                        .totalAmount(orderItem.getTotalAmount())
                        .build();
        }
}
