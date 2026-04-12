package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.MessageBossListDTO;
import cn.muzisheng.lebo.dto.MessageListDTO;
import cn.muzisheng.lebo.dto.SendMessageDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.InformationService;
import cn.muzisheng.lebo.vo.InformationBossVO;
import cn.muzisheng.lebo.vo.InformationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息接口，提供商家发送给客户消息和客户消息列表等接口
 */
@RestController
@RequestMapping("/information")
public class InformationApi {
    /**
     * 消息接口
     */
    private final InformationService informationService;
    public InformationApi(InformationService informationService) {
        this.informationService = informationService;
    }

    /**
     * 发送消息给客户，前端选择客户ID，创建消息记录
     * @param sendMessageDTO 消息信息，包含客户ID列表、消息主题、消息内容、消息类型，若客户ID列表为空，则发送给所有客户
     * @return 是否发送成功
     */
    @PostMapping("/sendMessage")
    public ResponseEntity<Result<Boolean>> sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        return informationService.sendMessage(sendMessageDTO);
    }
    /**
     * 客户获取本人接收到的消息分页列表，筛选条件为消息类型、分页页号、分页数量
     * 
     * @param messageListDTO 消息列表
     * @return 消息列表
     */
    @PostMapping("/messageList")
    public ResponseEntity<Result<IPage<InformationVO>>> messageList(MessageListDTO messageListDTO) {
        return informationService.messageList(messageListDTO);
    }
    /**
     * 商家获取消息分页列表,筛选条件为消息是否删除、消息类型、分页页号、分页数量、消息主题模糊查询、创建时间范围查询
     * 用户获取商家发送的消息列表
     * @param messageBossListDTO 消息列表
     * @return 消息列表
     */
    @PostMapping("/bossMessageList")
    public ResponseEntity<Result<IPage<InformationBossVO>>> bossMessageList(MessageBossListDTO messageBossListDTO) {
        return informationService.bossMessageList(messageBossListDTO);
    }

    
}
