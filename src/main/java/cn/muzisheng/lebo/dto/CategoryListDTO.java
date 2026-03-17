package cn.muzisheng.lebo.dto;

import lombok.Data;

/**
 * 分类列表查询参数
 */
@Data
public class CategoryListDTO {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 分类名称（模糊查询）
     */
    private String name;
}
