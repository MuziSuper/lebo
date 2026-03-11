package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.PointRecord;
import cn.muzisheng.lebo.mapper.PointRecordMapper;
import cn.muzisheng.lebo.service.PointRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PointRecordServiceImpl extends ServiceImpl<PointRecordMapper, PointRecord> implements PointRecordService {
    @Override
    public PointRecord createPointRecord(String openid, Integer defaultPoint) {
        PointRecord pointRecord = new PointRecord();
        return null;
    }
}
