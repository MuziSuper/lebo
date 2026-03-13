package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.InOutProductRecordSelectDTO;
import cn.muzisheng.lebo.entity.InOutProductRecord;
import cn.muzisheng.lebo.exception.ProductException;
import cn.muzisheng.lebo.mapper.InOutProductRecordMapper;
import cn.muzisheng.lebo.service.InOutProductRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class InOutProductRecordServiceImpl extends ServiceImpl<InOutProductRecordMapper, InOutProductRecord> implements InOutProductRecordService {
    /**
     * 获取所有商品入库记录列表，筛选条件有时间区间，商品名称，出入库类型, 商品ID
     * @param InOutProductRecordSelectDTO 筛选条件
     * @return 商品入库记录列表
     */
    @Override
    public ResponseEntity<List<InOutProductRecord>> getInOutRecordList(InOutProductRecordSelectDTO InOutProductRecordSelectDTO) {
        // 构建查询条件
        QueryWrapper<InOutProductRecord> queryWrapper = new QueryWrapper<>();
        
        if (InOutProductRecordSelectDTO != null) {
            // 商品ID筛选
            if (InOutProductRecordSelectDTO.getId() != null && !InOutProductRecordSelectDTO.getId().trim().isEmpty()) {
                queryWrapper.eq("product_id", InOutProductRecordSelectDTO.getId());
            }
            
            // 商品名称筛选（模糊查询）
            if (InOutProductRecordSelectDTO.getProductName() != null && !InOutProductRecordSelectDTO.getProductName().trim().isEmpty()) {
                queryWrapper.like("product_name", InOutProductRecordSelectDTO.getProductName());
            }
            
            // 出入库类型筛选
            if (InOutProductRecordSelectDTO.getType() != null) {
                if (InOutProductRecordSelectDTO.getType() != 1 && InOutProductRecordSelectDTO.getType() != 2) {
                    log.error("出入库类型错误, type: {}", InOutProductRecordSelectDTO.getType());
                    throw new ProductException("出入库类型错误，只能为1(入库)或2(出库)");
                }
                queryWrapper.eq("type", InOutProductRecordSelectDTO.getType());
            }
            
            // 时间区间筛选
            if (InOutProductRecordSelectDTO.getStartTime() != null && InOutProductRecordSelectDTO.getEndTime() != null) {
                if (InOutProductRecordSelectDTO.getStartTime().isAfter(InOutProductRecordSelectDTO.getEndTime())) {
                    log.error("开始时间不能晚于结束时间");
                    throw new ProductException("开始时间不能晚于结束时间");
                }
                queryWrapper.between("time", InOutProductRecordSelectDTO.getStartTime(), InOutProductRecordSelectDTO.getEndTime());
            } else if (InOutProductRecordSelectDTO.getStartTime() != null) {
                queryWrapper.ge("time", InOutProductRecordSelectDTO.getStartTime());
            } else if (InOutProductRecordSelectDTO.getEndTime() != null) {
                queryWrapper.le("time", InOutProductRecordSelectDTO.getEndTime());
            }
        }
        
        // 按时间倒序排列
        queryWrapper.orderByDesc("time");
        
        List<InOutProductRecord> recordList = this.list(queryWrapper);
        
        return ResponseEntity.ok(recordList);
    }
    /**
     * 创建商品出入库记录（单条）
     * @param InOutProductRecord 商品出入库记录
     */
    @Override
    public void addInOutRecordCount(InOutProductRecord InOutProductRecord){
        // 参数校验
        if (InOutProductRecord == null) {
            log.error("商品出入库记录不能为空");
            throw new ProductException("商品出入库记录不能为空");
        }
        
        if (InOutProductRecord.getProductId() == null || InOutProductRecord.getProductId().trim().isEmpty()) {
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        
        if (InOutProductRecord.getNumber() == null || InOutProductRecord.getNumber() <= 0) {
            log.error("商品出入库数量必须大于0");
            throw new ProductException("商品出入库数量必须大于0");
        }
        
        if (InOutProductRecord.getType() == null || (InOutProductRecord.getType() != 1 && InOutProductRecord.getType() != 2)) {
            log.error("出入库类型错误，只能为1(入库)或2(出库)");
            throw new ProductException("出入库类型错误，只能为1(入库)或2(出库)");
        }
        
        // 设置记录时间
        if (InOutProductRecord.getTime() == null) {
            InOutProductRecord.setTime(java.time.LocalDateTime.now());
        }
        
        // 保存记录
        if (!this.save(InOutProductRecord)) {
            log.error("商品出入库记录创建失败, productId: {}", InOutProductRecord.getProductId());
            throw new ProductException("商品出入库记录创建失败");
        }
        
        log.info("商品出入库记录创建成功, productId: {}, type: {}, number: {}", 
                InOutProductRecord.getProductId(), 
                InOutProductRecord.getType() == 1 ? "入库" : "出库", 
                InOutProductRecord.getNumber());
    }
    
    /**
     * 批量创建商品出入库记录
     * @param InOutProductRecordList 商品出入库记录列表
     */
    @Override
    public void addInOutRecordCountBatch(List<InOutProductRecord> InOutProductRecordList) {
        // 参数校验
        if (InOutProductRecordList == null || InOutProductRecordList.isEmpty()) {
            log.error("商品出入库记录列表不能为空");
            throw new ProductException("商品出入库记录列表不能为空");
        }
        
        // 校验每条记录
        for (InOutProductRecord record : InOutProductRecordList) {
            if (record == null) {
                log.error("商品出入库记录不能为空");
                throw new ProductException("商品出入库记录不能为空");
            }
            
            if (record.getProductId() == null || record.getProductId().trim().isEmpty()) {
                log.error("商品ID不能为空");
                throw new ProductException("商品ID不能为空");
            }
            
            if (record.getNumber() == null || record.getNumber() <= 0) {
                log.error("商品出入库数量必须大于0");
                throw new ProductException("商品出入库数量必须大于0");
            }
            
            if (record.getType() == null || (record.getType() != 1 && record.getType() != 2)) {
                log.error("出入库类型错误，只能为1(入库)或2(出库)");
                throw new ProductException("出入库类型错误，只能为1(入库)或2(出库)");
            }
            
            // 设置记录时间
            if (record.getTime() == null) {
                record.setTime(java.time.LocalDateTime.now());
            }
        }
        
        // 批量保存记录
        if (!this.saveBatch(InOutProductRecordList)) {
            log.error("商品出入库记录批量创建失败");
            throw new ProductException("商品出入库记录批量创建失败");
        }
        
        log.info("商品出入库记录批量创建成功, 共 {} 条记录", InOutProductRecordList.size());
    }
}
