package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.mapper.UserMapper;
import cn.muzisheng.lebo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
