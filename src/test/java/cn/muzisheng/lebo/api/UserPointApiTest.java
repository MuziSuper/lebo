package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.ConversionDTO;
import cn.muzisheng.lebo.service.UserPointService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class UserPointApiTest extends UnitTestSupport {

    @Mock
    private UserPointService userPointService;

    @Test
    void delegatesConvertEndpoint() {
        UserPointApi api = new UserPointApi(userPointService);
        ConversionDTO dto = new ConversionDTO();
        api.convert(dto);

        verify(userPointService).convert(dto);
    }
}
