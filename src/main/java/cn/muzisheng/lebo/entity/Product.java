package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.ProductStatusTypeHandler;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;

@PearObject(
        TableName = "product",
        pluralName = "商品",
        path = "product",
        desc = "商品管理",
        group = "商品"
)
@Entity(name = "product")
@Table(name = "product")
public class Product extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1024,name = "`description`")
    @TableField(value = "description")
    private String description;
    private String title;
    private String image;
    private String tags;
    @Column(length = 8)
    private String unit;
    @Column(name = "`category_id`")
    @TableField(value = "category_id")
    private Integer categoryId;
    @TableField(typeHandler = ProductStatusTypeHandler.class)
    private ProductStatusEnum status;
    @Column(name = "`sale_price`")
    @TableField(value = "sale_price")
    private Integer salePrice;
    @Column(name = "`vip_price`")
    @TableField(value = "vip_price")
    private Integer vipPrice;
    private Integer storage;
    @Column(name = "`cost_price`")
    @TableField(value = "cost_price")
    private Integer costPrice;
    private Integer point;
    @Column(name = "`is_point_convert`")
    @TableField(value = "is_point_convert")
    private Boolean isPointConvert;
}
