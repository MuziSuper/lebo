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
 * 历史操作记录，针对商户对商品的添加、修改、删除，对商品分类的添加、修改、删除，对后台系统的登录操作进行记录，以及对数据的手动备份和自动备份操作进行记录
 */
@Data
@Builder
public class HistoryOperation {
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
     * 操作类型，0: 商品添加，1: 商品修改，2: 商品删除，3: 手动备份数据，4: 自动备份数据，5: 后台系统登录 6: 商品分类添加，7: 商品分类修改，8: 商品分类删除
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

