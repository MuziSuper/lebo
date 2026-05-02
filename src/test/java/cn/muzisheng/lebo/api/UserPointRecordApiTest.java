package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.PointRecordListDTO;
import cn.muzisheng.lebo.service.PointRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class UserPointRecordApiTest extends UnitTestSupport {

    @Mock
    private PointRecordService pointRecordService;

    @Test
    void delegatesListEndpoint() {
        UserPointRecordApi api = new UserPointRecordApi(pointRecordService);
        PointRecordListDTO dto = new PointRecordListDTO();
        api.list(dto);

        verify(pointRecordService).getPointRecordByOrderId(dto);
    }
}
