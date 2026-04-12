package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 消息列表DTO,用于客户查询消息列表
 * @author muzisheng
 * @date 2023-08-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageListDTO {
    
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
}