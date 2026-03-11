package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.ProductStatusTypeHandler;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "product")
@Table(name = "product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    @Column(length = 1024,name = "`description`")
    @TableField(value = "description")
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
    @Column(length = 8)
    private String unit;
    /**
     * 商品类目ID
     */
    @Column(name = "`category_id`")
    @TableField(value = "category_id")
    private Long categoryId;
    /**
     * 商品状态,0: 上架, 1: 下架, 2: 删除
     */
    @TableField(typeHandler = ProductStatusTypeHandler.class)
    private ProductStatusEnum status;
    /**
     * 商品销售价格
     */
    @Column(name = "`sale_price`")
    @TableField(value = "sale_price")
    private Integer salePrice;
//    /**
//     * 商品会员价格
//     */
//    @Column(name = "`vip_price`")
//    @TableField(value = "vip_price")
//    private Integer vipPrice;
    /**
     * 商品库存
     */
    @Column(name = "`storage`")
    @TableField(value = "storage")
    private Long storage;
    /**
     * 商品成本价格
     */
    @Column(name = "`cost_price`")
    @TableField(value = "cost_price")
    private Integer costPrice;
    /**
     * 一单位商品产生积分
     */
    private Integer point;
    /**
     * 是否可积分兑换
     */
    @Column(name = "`is_point_convert`")
    @TableField(value = "is_point_convert")
    private Boolean isPointConvert;
    @Column(name = "gmt_created")
    @TableField(value = "gmt_created",fill = FieldFill.INSERT)
    private LocalDateTime gmtCreated;
    @Column(name = "gmt_modified")
    @TableField(value = "gmt_modified",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
    @Column(name = "is_deleted")
    @TableField(value = "is_deleted")
    private Boolean isDeleted;
}
