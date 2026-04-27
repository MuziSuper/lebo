package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryOperation {
    /**
     * 记录ID，格式：HIS_yyyyMMddHHmmss + 4位序列号
     */
    @Id
    @TableId(type = IdType.INPUT)
    private String id;
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
     * 操作时间
     */
    @Column(name = "time")
    @TableField("time")
    private LocalDateTime time;


}
