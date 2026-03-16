package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.PointRecordAddDTO;
import cn.muzisheng.lebo.dto.PointRecordListDTO;
import cn.muzisheng.lebo.entity.PointRecord;
import cn.muzisheng.lebo.mapper.PointRecordMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.PointRecordService;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import cn.muzisheng.lebo.vo.PointRecordListVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 积分记录服务实现类
 */
@Log4j2
@Service
public class PointRecordServiceImpl extends ServiceImpl<PointRecordMapper, PointRecord> implements PointRecordService {

    /**
     * 新增积分记录
     *
     * @param pointRecordAddDTO 积分记录
     * @return 是否成功
     */
    @Override
    public ResponseEntity<Result<Boolean>> addPointRecord(PointRecordAddDTO pointRecordAddDTO) {
        Response<Boolean> response = new Response<>();

        // 参数校验
        if (pointRecordAddDTO == null) {
            log.error("新增积分记录失败：参数为空");
            response.setError("新增积分记录失败：参数为空");
            return response.value();
        }

        // 获取用户openid（优先从DTO获取，否则从线程上下文获取）
        String openId = Optional.ofNullable(pointRecordAddDTO.getOpenId())
                .orElseGet(UserThreadUtil::getCurrentOpenId);

        if (StringUtils.isBlank(openId)) {
            log.error("新增积分记录失败：用户openid为空");
            response.setError("新增积分记录失败：用户openid为空");
            return response.value();
        }

        // 构建积分记录实体
        PointRecord pointRecord = new PointRecord();
        pointRecord.setOpenId(openId);
        pointRecord.setOrderId(pointRecordAddDTO.getOrderId());
        pointRecord.setDescription(pointRecordAddDTO.getDescription());
        pointRecord.setChangeAmount(pointRecordAddDTO.getChangeAmount());
        pointRecord.setBeforeAmount(pointRecordAddDTO.getBeforeAmount());
        pointRecord.setAfterAmount(pointRecordAddDTO.getAfterAmount());
        pointRecord.setGmtCreated(LocalDateTime.now());

        // 保存记录
        boolean saved = this.save(pointRecord);
        if (!saved) {
            log.error("新增积分记录失败：openId={}", openId);
            response.setError("新增积分记录失败");
            return response.value();
        }

        log.info("新增积分记录成功：openId={}, changeAmount={}", openId, pointRecordAddDTO.getChangeAmount());
        response.setData(true);
        return response.value();
    }

    /**
     * 查询积分记录，默认以创建时间倒序
     *
     * @param pointRecordListDTO 积分记录查询条件
     * @return 积分记录分页列表
     */
    @Override
    public ResponseEntity<Result<IPage<PointRecordListVO>>> getPointRecordByOrderId(PointRecordListDTO pointRecordListDTO) {
        Response<IPage<PointRecordListVO>> response = new Response<>();

        // 获取分页参数
        int pageNum = Optional.ofNullable(pointRecordListDTO)
                .map(PointRecordListDTO::getPageNum)
                .orElse(1);
        int pageSize = Optional.ofNullable(pointRecordListDTO)
                .map(PointRecordListDTO::getPageSize)
                .orElse(10);

        // 构建查询条件
        LambdaQueryWrapper<PointRecord> queryWrapper = new LambdaQueryWrapper<>();

        Optional.ofNullable(pointRecordListDTO).ifPresent(dto -> {
            // 用户openid精确查询
            Optional.ofNullable(dto.getOpenId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(openId -> queryWrapper.eq(PointRecord::getOpenId, openId));

            // 订单ID精确查询
            Optional.ofNullable(dto.getOrderId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(orderId -> queryWrapper.eq(PointRecord::getOrderId, orderId));

            // 记录ID精确查询
            Optional.ofNullable(dto.getId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(id -> queryWrapper.eq(PointRecord::getId, id));

            // 开始时间筛选
            Optional.ofNullable(dto.getStartTime())
                    .ifPresent(startTime -> queryWrapper.ge(PointRecord::getGmtCreated, startTime));

            // 结束时间筛选
            Optional.ofNullable(dto.getEndTime())
                    .ifPresent(endTime -> queryWrapper.le(PointRecord::getGmtCreated, endTime));
        });

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(PointRecord::getGmtCreated);

        // 分页查询
        Page<PointRecord> pageParam = new Page<>(pageNum, pageSize);
        IPage<PointRecord> resultPage = this.page(pageParam, queryWrapper);

        // 转换为VO
        IPage<PointRecordListVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<PointRecordListVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        response.setData(voPage);
        return response.value();
    }

    /**
     * 将实体转换为VO
     *
     * @param pointRecord 积分记录实体
     * @return 积分记录VO
     */
    private PointRecordListVO convertToVO(PointRecord pointRecord) {
        PointRecordListVO vo = new PointRecordListVO();
        BeanUtils.copyProperties(pointRecord, vo);
        return vo;
    }
}
