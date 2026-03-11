package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.PointRecord;

public interface PointRecordService {
    /**
     * 创建积分记录 已废弃
     * @param openid 用户openid
     * @param defaultPoint 默认积分
     * @return 创建的积分记录
     */
    PointRecord createPointRecord(String openid, Integer defaultPoint);
}
