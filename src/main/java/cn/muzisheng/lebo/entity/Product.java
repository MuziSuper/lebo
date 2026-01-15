package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.ProductStatusTypeHandler;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;
import lombok.Data;

@Data
@PearObject(
        TableName = "product",
        pluralName = "product",
        path = "product"
)
@Entity(name = "product")
@Table(name = "product")
public class Product extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
     * 商品标题
     */
    private String title;
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
     * 商品货币类型，eg: CNY、USD、JPY、EUR
     */
    private String currency;
    /**
     * 商品类目ID
     */
    @Column(name = "`category_id`")
    @TableField(value = "category_id")
    private Integer categoryId;
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
    /**
     * 商品会员价格
     */
    @Column(name = "`vip_price`")
    @TableField(value = "vip_price")
    private Integer vipPrice;
    /**
     * 商品库存
     */
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
}
