package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.PointRecordAddDTO;
import cn.muzisheng.lebo.dto.PointRecordListDTO;
import cn.muzisheng.lebo.entity.PointRecord;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.PointRecordListVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

/**
 * <p>
 * 积分记录 服务类接口
 * </p>
 *
 * @author 煲崽
 * @since 2020-08-10
 */
public interface PointRecordService extends IService<PointRecord> {
    /**
     * 新增积分记录
     * @param pointRecordAddDTO 积分记录
     * @return 是否成功
     */
    ResponseEntity<Result<Boolean>> addPointRecord(PointRecordAddDTO pointRecordAddDTO);
    /**
     * 查询积分记录，默认以创建时间倒序
     * @param pointRecordListDTO 积分记录查询条件
     * @return 积分记录
     */
    ResponseEntity<Result<IPage<PointRecordListVO>>> getPointRecordByOrderId(PointRecordListDTO pointRecordListDTO);
}
