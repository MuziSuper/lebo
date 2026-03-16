package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.InOutProductRecordSelectDTO;
import cn.muzisheng.lebo.entity.InOutProductRecord;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.InOutProductRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inOutProductRecord")
public class InOutProductRecordApi {
    private final InOutProductRecordService inOutProductRecordService;
    public InOutProductRecordApi(InOutProductRecordService inOutProductRecordService) {
        this.inOutProductRecordService = inOutProductRecordService;
    }

    /**
     * 获取所有商品入库记录列表，筛选条件有时间区间，商品名称，出入库类型, 商品ID
     * @param InOutProductRecordSelectDTO 筛选条件
     * @return 商品入库记录列表
     */
    @RequestMapping("/getInOutRecordList")
    public ResponseEntity<Result<IPage<InOutProductRecord>>> getInOutRecordList(InOutProductRecordSelectDTO InOutProductRecordSelectDTO) {
        return inOutProductRecordService.getInOutRecordList(InOutProductRecordSelectDTO);
    }

}
