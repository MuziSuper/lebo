package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.InformationDeleteDTO;
import cn.muzisheng.lebo.dto.MessageBossListDTO;
import cn.muzisheng.lebo.dto.MessageListDTO;
import cn.muzisheng.lebo.dto.SendMessageDTO;
import cn.muzisheng.lebo.service.InformationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class InformationApiTest extends UnitTestSupport {

    @Mock
    private InformationService informationService;

    @Test
    void delegatesAllInformationEndpoints() {
        InformationApi api = new InformationApi(informationService);
        SendMessageDTO sendDTO = new SendMessageDTO();
        MessageListDTO listDTO = new MessageListDTO();
        MessageBossListDTO bossListDTO = new MessageBossListDTO();
        InformationDeleteDTO deleteDTO = new InformationDeleteDTO();
        api.sendMessage(sendDTO);
        api.messageList(listDTO);
        api.bossMessageList(bossListDTO);
        api.look("i1");
        api.delete(deleteDTO);

        verify(informationService).sendMessage(sendDTO);
        verify(informationService).messageList(listDTO);
        verify(informationService).bossMessageList(bossListDTO);
        verify(informationService).look("i1");
        verify(informationService).delete(deleteDTO);
    }
}
