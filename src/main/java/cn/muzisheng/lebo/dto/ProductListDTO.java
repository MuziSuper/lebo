package cn.muzisheng.lebo.dto;

import lombok.Data;

@Data
public class ProductListDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
    private Long categoryId;
    private Integer status;
}
