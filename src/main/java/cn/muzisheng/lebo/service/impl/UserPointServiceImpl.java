package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.mapper.UserPointMapper;
import cn.muzisheng.lebo.service.UserPointService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class UserPointServiceImpl extends ServiceImpl<UserPointMapper, UserPoint> implements UserPointService {

    @Override
    @Transactional
    public UserPoint create(String openid, Long defaultPoint) throws UserPointException {
        UserPoint userPoint = new UserPoint();
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

    @Override
    @Transactional
    public Long getCurrentPoint(String openid) throws UserPointException {
        UserPoint userPoint = getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        return userPoint.getCurrentPoint();
    }

    @Transactional
    @Override
    public Long getAccumulatedPoint(String openid) throws UserPointException {
        UserPoint userPoint = getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        return userPoint.getAccumulatedPoint();
    }

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

    @Override
    public Boolean destroy(String openid) throws UserPointException {
        return this.remove(new QueryWrapper<UserPoint>().eq("open_id", openid));
    }
}
