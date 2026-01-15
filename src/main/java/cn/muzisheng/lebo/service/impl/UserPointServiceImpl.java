package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.mapper.UserPointMapper;
import cn.muzisheng.lebo.service.UserPointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserPointServiceImpl extends ServiceImpl<UserPointMapper, UserPoint> implements UserPointService {
}
