package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.HistoryOperationSelectDTO;
import cn.muzisheng.lebo.service.HistoryOperationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class HistoryOperationApiTest extends UnitTestSupport {

    @Mock
    private HistoryOperationService historyOperationService;

    @Test
    void delegatesListEndpoint() {
        HistoryOperationApi api = new HistoryOperationApi(historyOperationService);
        HistoryOperationSelectDTO dto = new HistoryOperationSelectDTO();
        api.getHistoryOperationList(dto);

        verify(historyOperationService).getHistoryOperation(dto);
    }
}
