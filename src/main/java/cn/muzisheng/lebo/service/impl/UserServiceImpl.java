package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.mapper.UserMapper;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.param.WXCodeSession;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.service.WXService;
import cn.muzisheng.lebo.utils.JwtUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final WXService wxService;
    private final JwtUtil jwtUtil;
    public UserServiceImpl(WXService wxService, JwtUtil jwtUtil) {
        this.wxService = wxService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 微信小程序登录
     *
     * @param code 小程序端传来的临时登录凭证
     * @return 用户的openid
     */
    @Override
    public ResponseEntity<Result<User>> login(String code) {
        Response<User> response = new Response<>();
        try {
            // 1. 调用微信code2Session接口获取openid和session_key
            WXCodeSession wxCodeSession = wxService.code2Session(code);

            // 2. 查询用户是否已存在
            User user=this.getById(wxCodeSession.getOpenId());

            if (user == null) {
                user=createUser(wxCodeSession);
            } else {
                // 4. 已存在用户，更新最后登录时间
                user=upadteUser(wxCodeSession);
            }
            response.putHeader("authorization", jwtUtil.generateToken(user.getOpenId()));
            return response.value();

        } catch (Exception e) {
            log.error("微信小程序登录失败", e);
            throw new RuntimeException("微信小程序登录失败: " + e.getMessage());
        }
    }

    private User upadteUser(WXCodeSession wxCodeSession){
        User user=this.getById(wxCodeSession.getOpenId());
        user.setLastLogin(LocalDateTime.now());
        user.setSessionKey(wxCodeSession.getSessionKey());
        user.setUnionId(wxCodeSession.getUnionId());
        user.setStatus(AccountStatusEnum.ACTIVE);
        if(this.updateById(user)){
            log.info("openid={},用户已存在，更新最后登录时间", wxCodeSession.getOpenId());
            return this.getById(user.getOpenId());
        }
        log.error("openid={},更新最后登录时间失败", wxCodeSession.getOpenId());
        throw new GeneralException("更新用户记录失败");
    }
    private User createUser(WXCodeSession wxCodeSession) {
        User user=new User();
        user.setOpenId(wxCodeSession.getOpenId());
        user.setUnionId(wxCodeSession.getUnionId());
        user.setLastLogin(LocalDateTime.now());
        user.setStatus(AccountStatusEnum.INACTIVE);
        if(this.save(user)){
            log.info("openid={}, 创建用户记录成功", wxCodeSession.getOpenId());
            return this.getById(user.getOpenId());
        }
        log.error("openid={}, 创建用户记录失败", wxCodeSession.getOpenId());
        throw new GeneralException("创建用户记录失败");
    }
    @Override
    public void updateLastLogin(String openid) {
        User user = new User();
        user.setOpenId(openid);
        user.setLastLogin(LocalDateTime.now());
        this.updateById(user);
        log.info("openid={}, 更新用户最后登录时间成功", openid);
    }
    @Override
    public User getUserByOpenId(String openid) {
        return this.getById(openid);
    }
}

