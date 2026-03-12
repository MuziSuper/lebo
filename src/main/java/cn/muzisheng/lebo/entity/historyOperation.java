package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 历史操作记录
 */
@Data
@Builder
public class historyOperation {
    /**
     * 记录ID,递增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 操作内容
     */
    @Column(name = "content")
    @TableField("content")
    private String content;
    /**
     * 操作类型，1: 商品添加，2: 商品修改，3: 商品删除，4: 手动备份数据，5: 自动备份数据，6: 后台系统登录 7: 商品分类添加，8: 商品分类修改，9: 商品分类删除
     */
    @Column(name = "type")
    @TableField("type")
    private Integer type;
    /**
     * 操作人ID
     */
    @Column(name = "operator_id")
    @TableField("operator_id")
    private String operatorId;
    /**
     * 操作人名称
     */
    @Column(name = "operator_name")
    @TableField("operator_name")
    private String operatorName;
    /**
     * 操作时间
     */
    @Column(name = "time")
    @TableField("time")
    private LocalDateTime time;


}

