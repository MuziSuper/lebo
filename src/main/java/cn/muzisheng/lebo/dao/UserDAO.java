package cn.muzisheng.lebo.dao;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.mapper.UserMapper;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import cn.muzisheng.lebo.param.WXCodeSession;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@Slf4j
@Data
public class UserDAO {

    @Autowired
    private UserMapper userMapper;

    public void updateLastLogin(String openid) {
        User user = new User();
        user.setOpenId(openid);
        user.setLast_login(LocalDateTime.now());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);

        userMapper.update(user, queryWrapper);
        log.info("openid={}, 更新用户最后登录时间成功", openid);
    }

}
