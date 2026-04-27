package cn.muzisheng.lebo.vo;

import lombok.Data;

import java.time.LocalDateTime;
@Data

public class InformationVO {
    /**
     * 消息主键ID
     */
    private String id;
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
     * 是否已查阅
     */
    private Boolean isLook;
    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;
}
