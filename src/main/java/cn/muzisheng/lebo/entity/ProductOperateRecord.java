package cn.muzisheng.lebo.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductOperateRecord {
    /**
     * 商品操作记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 操作类型,0: 添加, 1: 入库, 2: 出库， 3: 删除
     */
    private Integer operateType;
    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
    /**
     * 操作人ID
     */
    private Long openId;
}
