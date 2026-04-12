package cn.muzisheng.lebo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * 消息实体类,用于存储消息信息
 */
@Data
@Builder
public class Information {
    /**
     * 主键,UUID格式
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 消息ID,通过IdUtil生成,用于作为商户查询自己发送的消息时，对于发给多个用户的消息记作一条消息
     */
    @Column(name = "information_id")
    @TableField(value = "information_id")
    private String informationId;
    /**
     * 客户ID
     */
    @Column(name = "open_id")
    @TableField(value = "open_id")
    private String openId;
    /**
     * 客户名称
     */
    @Column(name = "name")
    @TableField(value = "name")
    private String name;
    /**
     * 消息主题
     */
    @Column(name = "subject")
    @TableField(value = "subject")
    private String subject;
    /**
     * 消息内容
     */
    @Column(name = "content")
    @TableField(value = "content")
    private String content;
    /**
     * 消息类型，0-通知消息，商家活动等 1-系统消息，系统维护或更新等 2-个人消息，客户订单信息或商户定点通信（积分扣除）等
     */
    @Column(name = "type")
    @TableField(value = "type")
    private Integer type;
    /**
     * 逻辑删除，默认0,逻辑删除的消息，商户端是可见的，但是客户端是不可见的
     * 0：未删除
     * 1：已删除
     */
    @Column(name = "is_deleted")
    @TableField(value = "is_deleted")
    private Integer deleted;
    /**
     * 创建时间
     */
    @Column(name = "gmt_created")
    @TableField(value = "gmt_created",fill = FieldFill.INSERT)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    @TableField(value = "gmt_modified",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
   }
