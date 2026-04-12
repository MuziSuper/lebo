package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.ConversionDTO;
import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.model.PointRecordTypeEnum;
import cn.muzisheng.lebo.model.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
     * @param point 增减的积分，可为正负，正积分则计入累加总积分
     * @param pointRecordType 积分记录类型
     */
    void updatePoint(String openid, Long point, PointRecordTypeEnum pointRecordType) throws UserPointException;
    /**
     * 销毁钱包，假删除
     * @param openid 用户openid
     * @return 是否成功销毁
     */
    Boolean destroy(String openid) throws UserPointException;
    
    /**
     * 根据openid列表批量查询用户积分钱包
     * @param openIds openid列表
     * @return 用户积分钱包列表
     */
    List<UserPoint> listByOpenIds(List<String> openIds);
    
    /**
     * 积分兑换商品
     * @param conversionDTO 积分兑换DTO，包含商品列表和描述
     * @return 是否兑换成功
     */
    ResponseEntity<Result<Boolean>> convert(ConversionDTO conversionDTO);
}
