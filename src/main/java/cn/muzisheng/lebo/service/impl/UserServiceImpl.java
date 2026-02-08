package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dao.UserDAO;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.mapper.UserMapper;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import cn.muzisheng.lebo.param.WXCodeSession;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.utils.WxUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private WxUtil wxUtil;
    @Autowired
    private UserDAO userDAO;

    /**
     * 微信小程序登录
     *
     * @param code 小程序端传来的临时登录凭证
     * @return 用户的openid
     */
    @Override
    public String login(String code) {

        try {
            // 1. 调用微信code2Session接口获取openid和session_key
            WXCodeSession wxCodeSession = wxUtil.code2Session(code);

            // 2. 查询用户是否已存在
            User user=this.getById(wxCodeSession.getOpenId());

            if (user == null) {
                user=createUser(wxCodeSession);
            } else {
                // 4. 已存在用户，更新最后登录时间
                user=upadteUser(wxCodeSession);
            }

            return ;

        } catch (Exception e) {
            log.error("微信小程序登录失败", e);
            throw new RuntimeException("微信小程序登录失败: " + e.getMessage());
        }
    }

    public User upadteUser(WXCodeSession wxCodeSession){
        User user=this.getById(wxCodeSession.getOpenId());
        user.setLast_login(LocalDateTime.now());
        if(this.updateById(user)){
            log.info("openid={},用户已存在，更新最后登录时间", wxCodeSession.getOpenId());
            return this.getById(user.getOpenId());
        }
        log.error("openid={},更新最后登录时间失败", wxCodeSession.getOpenId());
        return null;
    }
    public User createUser(WXCodeSession wxCodeSession) {
        User user=new User();
        user.setOpenId(wxCodeSession.getOpenId());
        user.setUnionId(wxCodeSession.getUnionId());
        user.setStatus(AccountStatusEnum.ACTIVE);
        user.setLast_login(LocalDateTime.now());
        if(this.save(user)){
            log.info("openid={}, 创建用户记录成功", wxCodeSession.getOpenId());
            return this.getById(user.getOpenId());
        }
        log.error("openid={}, 创建用户记录失败", wxCodeSession.getOpenId());
        return null;
    }


}
