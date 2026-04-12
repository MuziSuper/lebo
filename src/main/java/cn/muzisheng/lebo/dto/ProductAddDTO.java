package cn.muzisheng.lebo.dto;

import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.ProductException;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import cn.muzisheng.lebo.utils.IdUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class ProductAddDTO {
    /**
     * 商品ID
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 商品图片相对路径地址
     */
    private String image;
    /**
     * 商品标签，JSON格式的字符串数组
     */
    private String tags;
    /**
     * 商品单位，eg: 件、个、盒、箱
     */
    private String unit;
    /**
     * 商品类目ID
     */
    private Long categoryId;
    /**
     * 商品状态,0: 上架, 1: 下架, 2: 删除
     */
    private Integer status;
    /**
     * 商品销售价格
     */
    private Integer salePrice;
    /**
     * 商品库存
     */
    private Long storage;
    /**
     * 商品成本价格
     */
    private Integer costPrice;
    /**
     * 一单位商品产生积分
     */
    private Integer point;
    /**
     * 是否可积分兑换
     */
    private Boolean isPointConvert;
    /**
     * 兑换积分
     */
    private Long creditsExchange;
    public Product toProduct() {
        if (id==null){
            id = IdUtil.generateProductId();
        }
        if (status!=null&&!ProductStatusEnum.contains(status)) {
            log.error("商品状态不存在, status: {}", status);
            throw new ProductException("商品状态不存在, status: " + status);
        }

        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .image(image)
                .tags(tags)
                .unit(unit)
                .categoryId(categoryId)
                .status(ProductStatusEnum.fromCode(status))
                .salePrice(salePrice)
                .storage(storage)
                .costPrice(costPrice)
                .point(point)
                .isPointConvert(isPointConvert)
                .creditsExchange(creditsExchange)
                .build();
    }

    public Boolean isPointConvert() {
        return isPointConvert;
    }
}
