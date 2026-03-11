package cn.muzisheng.lebo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInOutDTO {
    private String productId;
    private Long number;
}
