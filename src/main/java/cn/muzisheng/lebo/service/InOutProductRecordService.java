package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.InOutProductRecord;

import java.util.List;

public interface InOutProductRecordService {
    List<InOutProductRecord> getInOutRecordList(InOutProductRecordSelectDTO InOutProductRecordSelectDTO);
}
