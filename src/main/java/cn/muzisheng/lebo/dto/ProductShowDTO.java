package cn.muzisheng.lebo.dto;

import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductShowDTO {
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
         * 商品图片地址
         */
        private String image;
        /**
         * 商品标签，JSON格式的字符串数组,eg: ["标签1", "标签2"]
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
        private ProductStatusEnum status;
        /**
         * 商品销售价格
         */
        private Integer salePrice;
        /**
         * 商品会员价格
         */
        private Integer vipPrice;
        /**
         * 商品库存
         */
        private Long storage;

        /**
         * 一单位商品产生积分
         */
        private Integer point;
        /**
         * 是否可积分兑换
         */
        private Boolean isPointConvert;
        /**
         * 将Product转为ProductShowDTO
         * @param product
         * @return ProductShowDTO
         */
        public static ProductShowDTO fromProduct(Product product) {
            return ProductShowDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .tags(product.getTags())
                        .unit(product.getUnit())
                        .categoryId(product.getCategoryId())
                        .status(product.getStatus())
                        .salePrice(product.getSalePrice())
                        .storage(product.getStorage())
                        .point(product.getPoint())
                        .isPointConvert(product.getIsPointConvert())
                        .build();
        }
}
