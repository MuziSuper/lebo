package cn.muzisheng.lebo.dto;

import cn.muzisheng.lebo.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据备份DTO
 * 用于封装全量备份数据（商品、类目、订单、订单项、用户、用户积分、商品出入库记录、积分记录）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupDataDTO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 所有商品数据
     */
    private List<Product> products;
    
    /**
     * 所有商品类目数据
     */
    private List<Category> categories;
    
    /**
     * 所有订单数据
     */
    private List<Order> orders;
    
    /**
     * 所有订单项数据
     */
    private List<OrderItem> orderItems;
    
    /**
     * 所有用户数据
     */
    private List<User> users;
    
    /**
     * 所有用户积分数据
     */
    private List<UserPoint> userPoints;
    
    /**
     * 所有商品出入库记录
     */
    private List<InOutProductRecord> inOutProductRecords;
    
    /**
     * 所有积分记录
     */
    private List<PointRecord> pointRecords;
}
