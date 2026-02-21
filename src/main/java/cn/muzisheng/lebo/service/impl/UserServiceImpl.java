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
import cn.muzisheng.lebo.utils.UserThreadUtil;
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
        // 创建响应对象
        Response<User> response = new Response<>();
        try {
            // 调用微信服务，通过code获取用户的openid和session_key
            WXCodeSession wxCodeSession = wxService.code2Session(code);

            User user = this.getById(wxCodeSession.getOpenId());

            if (user == null) {
                user = createUser(wxCodeSession);
            } else {
                user = upadteUser(wxCodeSession);
            }
            // 生成JWT token并放入响应头中，用于后续接口的身份验证
            response.putHeader("Authorization", jwtUtil.generateToken(user.getOpenId()));
            // 返回响应结果
            return response.value();

        } catch (Exception e) {
            // 记录登录失败日志
            log.error("微信小程序登录失败", e);
            // 抛出运行时异常，返回错误信息给前端
            throw new GeneralException("微信小程序登录失败: " + e.getMessage());
        }
    }

    /**
     * 微信小程序退出登录
     *
     */
    public ResponseEntity<Result<Boolean>> logout() {
        // 创建响应对象
        Response<Boolean> response = new Response<>();
        String openid= UserThreadUtil.getCurrentOpenId();
        User user = this.getById(openid);
        if (user == null) {
            throw new GeneralException("用户不存在");
        }
        user.setLastLogin(LocalDateTime.now());
        if(!this.updateById(user)){
            throw new GeneralException("更新用户最后登录时间失败");
        }
       response.setData(true);
        return response.value();
    }
    /**
     * 更新用户信息
     * 主要用于微信登录后更新用户最后登录时间、sessionKey等信息
     *
     * @param wxCodeSession 微信登录返回的会话信息（包含openId、sessionKey、unionId等）
     * @return 更新后的用户对象
     * @throws GeneralException 当更新数据库失败时抛出异常
     */
    private User upadteUser(WXCodeSession wxCodeSession) {
        User user = this.getById(wxCodeSession.getOpenId());
        if (user == null) {
            throw new GeneralException("用户不存在");
        }
        // 更新用户最后登录时间为当前时间
        user.setLastLogin(LocalDateTime.now());
        // 更新微信会话密钥（用于后续与微信服务器交互）
        user.setSessionKey(wxCodeSession.getSessionKey());
        // 更新微信unionId（同一用户在多个微信应用中的唯一标识）
        user.setUnionId(wxCodeSession.getUnionId());
        // 设置用户账户状态为"活跃"
        user.setStatus(AccountStatusEnum.ACTIVE);
        // 执行数据库更新操作
        if (this.updateById(user)) {
            // 更新成功，记录日志
            log.info("openid={},用户已存在，更新最后登录时间", wxCodeSession.getOpenId());
            // 重新查询并返回更新后的完整用户信息
            return this.getById(user.getOpenId());
        }
        // 更新失败，记录错误日志
        log.error("openid={},更新最后登录时间失败", wxCodeSession.getOpenId());
        // 抛出业务异常
        throw new GeneralException("更新用户记录失败");
    }

    /**
     * 创建用户记录
     *
     * @param wxCodeSession 微信登录返回的会话信息（包含openId、unionId等）
     * @return 创建并激活后的用户对象
     * @throws GeneralException 当用户记录创建失败时抛出
     */
    private User createUser(WXCodeSession wxCodeSession) {
        User user = new User();
        // 设置微信开放平台唯一标识
        user.setOpenId(wxCodeSession.getOpenId());
        // 设置微信开放平台统一标识（跨应用）
        user.setUnionId(wxCodeSession.getUnionId());
        // 记录当前时间为最后登录时间
        user.setLastLogin(LocalDateTime.now());
        // 初始状态设为"未激活"
        user.setStatus(AccountStatusEnum.INACTIVE);
        if (!this.save(user)) {
            log.error("openid={}, 创建用户记录失败", wxCodeSession.getOpenId());
            // 保存失败则抛出异常
            throw new GeneralException("创建用户记录失败");
        }
        log.info("openid={}, 创建用户记录成功", wxCodeSession.getOpenId());
        user = this.getById(user.getOpenId());
        if(user == null){
            throw new GeneralException("用户不存在");
        }
        user.setStatus(AccountStatusEnum.ACTIVE);
        // 更新用户状态到数据库
        if (!this.save(user)) {
            log.error("openid={}, 更新用户状态失败", wxCodeSession.getOpenId());
            throw new GeneralException("更新用户状态失败");
        }
        user = this.getById(user.getOpenId());
        if(user == null){
            throw new GeneralException("用户不存在");
        }
        log.info("openid={}, 更新用户状态成功", wxCodeSession.getOpenId());
        return user;
    }

    @Override
    public void updateLastLogin(String openid) {
        // 创建用户对象
        User user = new User();
        // 设置用户openid（作为唯一标识）
        user.setOpenId(openid);
        // 设置最后登录时间为当前系统时间
        user.setLastLogin(LocalDateTime.now());
        // 根据ID（openid）更新用户信息到数据库
        this.updateById(user);
        // 记录日志：更新用户最后登录时间成功
        log.info("openid={}, 更新用户最后登录时间成功", openid);
    }

    @Override
    public User getUserByOpenId(String openid) {
        //   直接调用父类的getById方法，将openid作为主键进行查询
        return this.getById(openid);
    }
}

