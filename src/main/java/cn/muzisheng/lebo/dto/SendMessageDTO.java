package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO {
    /**
     * 客户ID列表，为空则发送给全部客户
     */
    private List<String> openIds;
    
    /**
     * 消息主题
     */
    private String subject;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息类型，0-通知消息，1-系统消息，2-个人消息
     */
    private Integer type;
}
