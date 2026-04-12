package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 消息列表DTO,用于商户查询消息列表
 * @author muzisheng
 * @date 2023-08-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageBossListDTO {
    /**
     * 分页页号
     */
    private Integer pageNum;
    /**
     * 分页数量
     */
    private Integer pageSize;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息主题的模糊查询
     */
    private String likeSubject;
    /**
     * 创建时间开始点
     */
    private LocalDateTime startGmtCreated;
    /**
     * 创建时间结束点
     */
    private LocalDateTime endGmtCreated;
    /**
     * 逻辑删除，默认0,逻辑删除的消息，商户端是可见的，但是客户端是不可见的
     * 0：未删除
     * 1：已删除
     */
    private Integer deleted;

}
