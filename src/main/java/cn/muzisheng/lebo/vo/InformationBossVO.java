package cn.muzisheng.lebo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
/**
 * 消息VO,用于存储消息信息
 */
@Data
public class InformationBossVO {
    /**
     * 消息唯一ID
     */
    private String id;
    /**
     * 消息批次ID
     */
    private String informationId;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息主题
     */
    private String subject;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;
    /**
     * 逻辑删除，默认0,逻辑删除的消息，商户端是可见的，但是客户端是不可见的
     * 0：未删除
     * 1：已删除
     */
    private Integer deleted;
    /**
     * 接收消息的用户ID列表
     */
    private List<String> openIds;
}
