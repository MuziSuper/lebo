package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.MessageBossListDTO;
import cn.muzisheng.lebo.dto.InformationDeleteDTO;
import cn.muzisheng.lebo.dto.MessageListDTO;
import cn.muzisheng.lebo.dto.SendMessageDTO;
import cn.muzisheng.lebo.entity.Information;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.InformationBossVO;
import cn.muzisheng.lebo.vo.InformationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

public interface InformationService extends IService<Information> {
    
    ResponseEntity<Result<Boolean>> sendMessage(SendMessageDTO sendMessageDTO);
    
    ResponseEntity<Result<IPage<InformationVO>>> messageList(MessageListDTO messageListDTO);
    
    ResponseEntity<Result<IPage<InformationBossVO>>> bossMessageList(MessageBossListDTO messageBossListDTO);

    /**
     * 按消息批次ID删除消息
     *
     * @param informationDeleteDTO 删除参数
     * @return 是否删除成功
     */
    ResponseEntity<Result<Boolean>> delete(InformationDeleteDTO informationDeleteDTO);

    /**
     * 用户查阅消息，将对应消息更新为已查阅状态
     *
     * @param id 消息主键ID
     * @return 已查阅消息主键ID
     */
    ResponseEntity<Result<String>> look(String id);
    
    /**
     * 保存单条消息记录
     * @param information 消息实体
     * @return 是否保存成功
     */
    boolean saveInformation(Information information);
}
