package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.InOutProductRecordSelectDTO;
import cn.muzisheng.lebo.service.InOutProductRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class InOutProductRecordApiTest extends UnitTestSupport {

    @Mock
    private InOutProductRecordService inOutProductRecordService;

    @Test
    void delegatesListEndpoint() {
        InOutProductRecordApi api = new InOutProductRecordApi(inOutProductRecordService);
        InOutProductRecordSelectDTO dto = new InOutProductRecordSelectDTO();
        api.getInOutRecordList(dto);

        verify(inOutProductRecordService).getInOutRecordList(dto);
    }
}
