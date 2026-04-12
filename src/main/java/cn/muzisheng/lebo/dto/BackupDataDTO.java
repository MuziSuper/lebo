package cn.muzisheng.lebo.dto;

import cn.muzisheng.lebo.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 备份数据DTO
 * 用于封装全量备份数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupDataDTO implements Serializable {
    
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
    
    /**
     * 所有用户签到记录
     */
    private List<UserSignIn> userSignIns;
    
    /**
     * 所有历史操作记录
     */
    private List<HistoryOperation> historyOperations;
    
    /**
     * 所有消息通知记录
     */
    private List<Information> informations;
}
