package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.model.Awards;
import cn.muzisheng.lebo.service.AwardRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.verify;

class AwardApiTest extends UnitTestSupport {

    @Mock
    private AwardRecordService awardRecordService;

    @Test
    void delegatesAllAwardEndpoints() {
        AwardApi api = new AwardApi(awardRecordService);
        List<Awards> awards = List.of();
        api.updateAwards(awards);
        api.getAwardsList();
        api.drawAward();

        verify(awardRecordService).updateAwards(awards);
        verify(awardRecordService).getAwardsList();
        verify(awardRecordService).drawAward();
    }
}
