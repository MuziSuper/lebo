package cn.muzisheng.lebo.dto;

import lombok.Data;

import java.util.List;
/**
 * 订单添加DTO
 */
@Data
public class OrderAddDTO {
    /**
     * 房间号
     */
    private String homeNumber;

    /**
     * 购买商品列表
     * @see ProductInOutDTO
     */
    private List<ProductOutDTO> productOutDTOList;
}
