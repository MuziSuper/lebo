package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.HistoryOperationSelectDTO;
import cn.muzisheng.lebo.entity.HistoryOperation;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.HistoryOperationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 历史操作记录API控制器
 */
@RestController
@RequestMapping("/historyOperation")
public class HistoryOperationApi {
    private final HistoryOperationService historyOperationService;

    public HistoryOperationApi(HistoryOperationService historyOperationService) {
        this.historyOperationService = historyOperationService;
    }

    /**
     * 获取历史操作记录列表（分页）
     * @param dto 查询条件DTO
     * @return 分页历史操作记录
     */
    @RequestMapping("/getHistoryOperationList")
    public ResponseEntity<Result<IPage<HistoryOperation>>> getHistoryOperationList(@RequestBody HistoryOperationSelectDTO dto) {
        return historyOperationService.getHistoryOperation(dto);
    }
}
