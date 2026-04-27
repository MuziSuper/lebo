package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.HistoryOperationSelectDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.HistoryOperationService;
import cn.muzisheng.lebo.vo.HistoryOperationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historyOperation")
public class HistoryOperationApi {
    private final HistoryOperationService historyOperationService;

    public HistoryOperationApi(HistoryOperationService historyOperationService) {
        this.historyOperationService = historyOperationService;
    }

    @RequestMapping("/getHistoryOperationList")
    public ResponseEntity<Result<IPage<HistoryOperationVO>>> getHistoryOperationList(@RequestBody HistoryOperationSelectDTO dto) {
        return historyOperationService.getHistoryOperation(dto);
    }
}
