package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.exception.UserPointException;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserPointService extends IService<UserPoint> {
    /**
     * 创建用户积分钱包
     * @param openid 用户openid
     * @param defaultPoint 默认积分
     * @return 创建的用户积分钱包
     * @throws UserPointException 当用户积分钱包创建失败时抛出
     */
    UserPoint create(String openid, Long defaultPoint) throws UserPointException;
    /**
     * 创建用户积分钱包,默认为空
     * @param openid 用户openid
     * @return 创建的用户积分钱包
     * @throws UserPointException 当用户积分钱包创建失败时抛出
     */
    UserPoint create(String openid) throws  UserPointException;
    /**
     * 获取用户积分钱包当前积分
     * @param openid 用户openid
     * @return 当前积分
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    Long getCurrentPoint(String openid) throws UserPointException;
    /**
     * 获取用户积分钱包累计总积分
     * @param openid 用户openid
     * @return 历史总积分
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    Long getAccumulatedPoint(String openid) throws UserPointException;
    /**
     * 获取用户积分钱包积分记录
     * @param openid 用户openid
     * @return 积分记录
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    UserPoint getPointRecord(String openid) throws UserPointException;
    /**
     * 修改用户积分钱包积分
     * @param openid 用户openid
     * @param point 修改的积分，可为正负，正积分则计入累加总积分
     * @return 修改后的钱包
     */
    UserPoint updatePoint(String openid, Long point) throws UserPointException;
    /**
     * 销毁钱包，假删除
     * @param openid 用户openid
     * @return 是否成功销毁
     */
    Boolean destroy(String openid) throws UserPointException;
}
