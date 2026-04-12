package cn.muzisheng.lebo.dto;

import lombok.Data;

@Data
public class ProductListDTO {
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 关键词
     */
    private String keyword;
    /**
     * 类目ID
     */
    private Long categoryId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否可积分兑换
     */
    private Boolean isPointConvert;
}
