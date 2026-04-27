package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
        private String productId;
        private String productName;
        private String productImage;
        private Integer onePrice;
        private Long quantity;
        private Long totalAmount;
        public static OrderItemVO fromOrderItem(OrderItem orderItem, String productImage) {
                return OrderItemVO.builder()
                        .productId(orderItem.getProductId())
                        .productName(orderItem.getProductName())
                        .productImage(productImage)
                        .onePrice(orderItem.getOnePrice())
                        .quantity(orderItem.getQuantity())
                        .totalAmount(orderItem.getTotalAmount())
                        .build();
        }
}
