package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.InOutProductRecordSelectDTO;
import cn.muzisheng.lebo.entity.InOutProductRecord;
import cn.muzisheng.lebo.model.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InOutProductRecordService {
    /**
     * 获取所有商品出入库记录列表（分页），筛选条件有时间区间，商品名称，出入库类型, 商品ID
     * @param InOutProductRecordSelectDTO 筛选条件
     * @return 商品出入库记录分页列表
     */
    ResponseEntity<Result<IPage<InOutProductRecord>>> getInOutRecordList(InOutProductRecordSelectDTO InOutProductRecordSelectDTO);
    
    /**
     * 创建商品出入库记录（单条）
     * @param InOutProductRecord 商品出入库记录
     */
    void addInOutRecordCount(InOutProductRecord InOutProductRecord);
    
    /**
     * 批量创建商品出入库记录
     * @param InOutProductRecordList 商品出入库记录列表
     */
    void addInOutRecordCountBatch(List<InOutProductRecord> InOutProductRecordList);
}
