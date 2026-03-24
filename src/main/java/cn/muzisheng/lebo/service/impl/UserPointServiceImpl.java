package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.mapper.UserPointMapper;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.utils.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
public class UserPointServiceImpl extends ServiceImpl<UserPointMapper, UserPoint> implements UserPointService {
    /**
     * 创建用户积分钱包
     * @param openid 用户openid
     * @param defaultPoint 默认积分
     * @return 创建的用户积分钱包
     * @throws UserPointException 当用户积分钱包创建失败时抛出
     */
    @Override
    @Transactional
    public UserPoint create(String openid, Long defaultPoint) throws UserPointException {
        UserPoint userPoint = new UserPoint();
        userPoint.setId(IdUtil.generateUserPointId());
        userPoint.setOpenId(openid);
        userPoint.setAccumulatedPoint(defaultPoint);
        userPoint.setCurrentPoint(defaultPoint);
        if (!this.save(userPoint)) {
            log.error("openid={}, 创建用户积分记录失败", openid);
            throw new UserPointException("创建用户积分记录失败");
        }
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);
        return this.getOne(queryWrapper);
    }
    /**
     * 创建用户积分钱包,默认为空
     * @param openid 用户openid
     * @return 创建的用户积分钱包
     * @throws UserPointException 当用户积分钱包创建失败时抛出
     */
    @Override
    @Transactional
    public UserPoint create(String openid) {
        UserPoint userPoint = new UserPoint();
        userPoint.setOpenId(openid);
        userPoint.setAccumulatedPoint(0L);
        userPoint.setCurrentPoint(0L);
        if (!this.save(userPoint)) {
            log.error("openid={}, 创建用户积分记录失败", openid);
            throw new UserPointException("创建用户积分记录失败");
        }
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);
        return this.getOne(queryWrapper);
    }
    /**
     * 获取用户积分钱包当前积分
     * @param openid 用户openid
     * @return 当前积分
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    @Override
    @Transactional(readOnly = true)
    public Long getCurrentPoint(String openid) throws UserPointException {
        UserPoint userPoint = getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        if (userPoint == null) {
            log.error("openid={}, 获取用户积分记录失败", openid);
            throw new UserPointException("获取用户积分记录失败");
        }
        return userPoint.getCurrentPoint();
    }
    /**
     * 获取用户积分钱包累计总积分
     * @param openid 用户openid
     * @return 历史总积分
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    @Transactional(readOnly = true)
    @Override
    public Long getAccumulatedPoint(String openid) throws UserPointException {
        UserPoint userPoint = getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        if (userPoint == null) {
            log.error("openid={}, 获取用户积分记录失败", openid);
            throw new UserPointException("获取用户积分记录失败");
        }
        return userPoint.getAccumulatedPoint();
    }
    /**
     * 获取用户积分钱包积分记录
     * @param openid 用户openid
     * @return 积分记录
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    @Override
    public UserPoint getPointRecord(String openid) throws UserPointException {
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);
        UserPoint userPoint = this.getOne(queryWrapper);
        if (userPoint == null) {
            log.error("openid={}, 获取用户积分记录失败", openid);
            throw new UserPointException("获取用户积分记录失败");
        }
        return userPoint;
    }
    /**
     * 修改用户积分钱包积分
     * @param openid 用户openid
     * @param point 修改的积分，可为正负，正积分则计入累加总积分
     * @return 修改后的钱包
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = UserPointException.class)
    @Override
    public UserPoint updatePoint(String openid, @NonNull Long point) throws UserPointException {

        UserPoint userPoint = this.getOne(new QueryWrapper<UserPoint>().eq("open_id", openid).last("FOR UPDATE"));
        if (userPoint == null) {
            log.error("openid={}, 获取原用户积分记录失败", openid);
            throw new UserPointException("获取原用户积分记录失败");
        }
        long newPoint = userPoint.getCurrentPoint() + point;
        if (newPoint < 0) {
            log.error("openid={}, 原用户积分不足", openid);
            throw new UserPointException("原用户积分不足");
        }
        UpdateWrapper<UserPoint> updateWrapper = new UpdateWrapper<>();
        if (point > 0) {
            long newAccumulatedPoint = userPoint.getAccumulatedPoint() + point;
            updateWrapper.set("accumulated_point", newAccumulatedPoint);
        }
        long newCurrentPoint = userPoint.getCurrentPoint() + point;
        updateWrapper.eq("open_id", openid);
        updateWrapper.set("current_point", newCurrentPoint);
        if (!this.update(updateWrapper)) {
            log.error("openid={}, 更新用户积分记录失败", openid);
            throw new UserPointException("更新用户积分记录失败");
        }
        UserPoint newUserPoint = this.getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        if (newUserPoint == null) {
            log.error("openid={}, 获取更新后的用户积分记录失败", openid);
            throw new UserPointException("获取更新后的用户积分记录失败");
        }
        return newUserPoint;
    }
    /**
     * 销毁钱包，假删除
     * @param openid 用户openid
     * @return 是否成功销毁
     */
    @Override
    @Transactional
    public Boolean destroy(String openid) throws UserPointException {
        // 使用逻辑删除而非物理删除
        UpdateWrapper<UserPoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("open_id", openid);
        updateWrapper.set("is_deleted", 1);
        boolean result = this.update(updateWrapper);
        if (!result) {
            log.error("openid={}, 销毁用户积分钱包失败", openid);
            throw new UserPointException("销毁用户积分钱包失败");
        }
        return true;
    }
    
    /**
     * 根据openid列表批量查询用户积分钱包
     * @param openIds openid列表
     * @return 用户积分钱包列表
     */
    @Override
    public List<UserPoint> listByOpenIds(List<String> openIds) {
        if (openIds == null || openIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("open_id", openIds);
        return this.list(queryWrapper);
    }
}
