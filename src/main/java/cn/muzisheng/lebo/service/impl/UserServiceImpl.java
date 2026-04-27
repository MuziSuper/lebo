package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.BossLoginDTO;
import cn.muzisheng.lebo.dto.LoginDTO;
import cn.muzisheng.lebo.dto.UserListDTO;
import cn.muzisheng.lebo.dto.UserOpenIdsDTO;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.exception.UserException;
import cn.muzisheng.lebo.mapper.UserMapper;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.param.WXCodeSession;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.service.WXService;
import cn.muzisheng.lebo.utils.JwtUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import cn.muzisheng.lebo.vo.LoginVO;
import cn.muzisheng.lebo.vo.UserListVO;
import cn.muzisheng.lebo.vo.UserUpdateVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Log4j2
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final WXService wxService;
    private final JwtUtil jwtUtil;
    private final UserPointService userPointService;

    public UserServiceImpl(WXService wxService, JwtUtil jwtUtil, UserPointService userPointService) {
        this.wxService = wxService;
        this.jwtUtil = jwtUtil;
        this.userPointService = userPointService;
    }

    /**
     * 微信小程序登录
     *
     * @param code 小程序端传来的临时登录凭证
     * @return 登录结果
     */
    @Override
    public ResponseEntity<Result<Boolean>> login(String code) {
        // 创建响应对象
        Response<Boolean> response = new Response<>();
        // 调用微信服务，通过code获取用户的openid和session_key
        WXCodeSession wxCodeSession = wxService.code2Session(code);

        User user = this.getById(wxCodeSession.getOpenId());
        UserThreadUtil.setCurrentOpenId(wxCodeSession.getOpenId());
        // 如果用户不存在，则创建用户记录和用户积分钱包
        if (user == null) {
            log.info("openid={},用户不存在", wxCodeSession.getOpenId());
            response.setData(false);
            return response.value();
        }
        log.info("openid={}，更新用户信息", wxCodeSession.getOpenId());
        user = updateUser(wxCodeSession);
        log.info("openid={},用户登录成功", wxCodeSession.getOpenId());
        // 生成JWT token并放入响应头中，用于后续接口的身份验证
        response.putHeader("Authorization", jwtUtil.generateToken(user.getOpenId()));
        response.setData(true);
        // 返回响应结果
        return response.value();
    }

    public ResponseEntity<Result<Boolean>> bossLogin(BossLoginDTO bossLoginDTO){
        // 创建响应对象
        Response<Boolean> response = new Response<>();
        String nickName = Optional.ofNullable(bossLoginDTO).map(BossLoginDTO::getNickName).orElse(null);
        String password = Optional.ofNullable(bossLoginDTO).map(BossLoginDTO::getPassword).orElse(null);
        if(nickName==null||password==null){
            log.info("用户名或密码不能为空");
            throw new UserException("用户名或密码不能为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nick_name",nickName);
        User user = this.getOne(queryWrapper);
        if(user==null){
            log.info("用户名不存在");
            throw new UserException("用户名不存在");
        }
        String usePassword = Optional.of(user).map(User::getPassword).orElse(null);
        if(usePassword==null){
            log.info("用户密码为空,异常登录");
            throw new UserException("用户密码为空,异常登录");
        }
        if(!usePassword.equals(password)){
            log.info("用户密码错误,异常登录");
            throw new UserException("用户密码错误,异常登录");
        }
        log.info("用户登录成功");
        response.putHeader("Authorization", jwtUtil.generateToken(user.getOpenId()));
        response.setData(true);
        return response.value();
    }


    /**
     * 微信小程序登录
     *
     * @param code 小程序端传来的临时登录凭证
     * @param loginDTO 用户登录信息
     * @return  登录结果
     */
    @Override
    public ResponseEntity<Result<Boolean>> register(String code, LoginDTO loginDTO) {
        // 创建响应对象
        Response<Boolean> response = new Response<>();
        // 调用微信服务，通过code获取用户的openid和session_key
        WXCodeSession wxCodeSession = wxService.code2Session(code);

        User user = this.getById(wxCodeSession.getOpenId());
        UserThreadUtil.setCurrentOpenId(wxCodeSession.getOpenId());
        // 如果用户不存在，则创建用户记录和用户积分钱包
        if (user != null) {
            log.info("openid={},用户已存在，更新用户信息", wxCodeSession.getOpenId());
            user = updateUser(wxCodeSession);
        } else {
            log.info("openid={},用户不存在，创建用户记录", wxCodeSession.getOpenId());
            user = createUser(wxCodeSession, loginDTO);
            userPointService.create(user.getOpenId());
        }
        log.info("openid={},用户登录成功", wxCodeSession.getOpenId());
        // 生成JWT token并放入响应头中，用于后续接口的身份验证
        response.putHeader("Authorization", jwtUtil.generateToken(user.getOpenId()));
        response.setData(true);
        // 返回响应结果
        return response.value();
    }

    /**
     * 根据请求体中的AuthToken获取用户信息
     *
     * @return 当前登录用户的信息
     */
    @Override
    public ResponseEntity<Result<LoginVO>> info() {
        // 创建响应对象
        Response<LoginVO> response = new Response<>();
        // 获取当前登录用户的openid
        String openid = UserThreadUtil.getCurrentOpenId();
        // 根据openid查询用户信息
        User user = this.getUserByOpenId(openid);
        // 获取用户积分记录
        UserPoint userPoint = userPointService.getPointRecord(openid);
        // 封装响应数据
        response.setData(LoginVO.of(user, userPoint));
        return response.value();
    }

    public ResponseEntity<Result<Boolean>> update(UserUpdateVO userUpdateVO) {
        // 创建响应对象
        Response<Boolean> response = new Response<>();
        String openId = UserThreadUtil.getCurrentOpenId();
        User updateUser = UserUpdateVO.of(userUpdateVO);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("open_id", openId);
        if (!this.update(updateUser, updateWrapper)) {
            throw new UserException("更新用户信息失败");
        }
        response.setData(true);
        return response.value();
    }

    /**
     * 微信小程序退出登录
     */
    public ResponseEntity<Result<Boolean>> logout() {
        // 创建响应对象
        Response<Boolean> response = new Response<>();
        String openid = UserThreadUtil.getCurrentOpenId();
        User user = this.getById(openid);
        if (user == null) {
            throw new GeneralException("用户不存在");
        }
        user.setLastLogin(LocalDateTime.now());
        if (!this.updateById(user)) {
            throw new UserException("更新用户最后登录时间失败");
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
    private User updateUser(WXCodeSession wxCodeSession) {
        User user = this.getById(wxCodeSession.getOpenId());
        if (user == null) {
            throw new UserException("用户不存在");
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
            log.info("openid={}, 更新最后登录时间", wxCodeSession.getOpenId());
            // 重新查询并返回更新后的完整用户信息
            return this.getById(user.getOpenId());
        }
        // 更新失败，记录错误日志
        log.error("openid={},更新最后登录时间失败", wxCodeSession.getOpenId());
        // 抛出业务异常
        throw new UserException("更新用户记录失败");
    }

    /**
     * 创建用户记录
     *
     * @param wxCodeSession 微信登录返回的会话信息（包含openId、unionId等）
     * @return 创建并激活后的用户对象
     * @throws GeneralException 当用户记录创建失败时抛出
     */
    private User createUser(WXCodeSession wxCodeSession, LoginDTO loginDTO) {
        User user = new User();
        // 设置微信开放平台唯一标识
        user.setOpenId(wxCodeSession.getOpenId());
        // 设置微信开放平台统一标识（跨应用）
        user.setUnionId(wxCodeSession.getUnionId());
        // 设置微信会话密钥（用于后续与微信服务器交互）
        user.setSessionKey(wxCodeSession.getSessionKey());
        // 记录当前时间为最后登录时间
        user.setLastLogin(LocalDateTime.now());
        // 初始状态设为"未激活"
        user.setStatus(AccountStatusEnum.INACTIVE);
        if(loginDTO!=null){
            user.setNickName(loginDTO.getNickName());
            user.setAvatar(loginDTO.getAvatar());
            user.setGender(loginDTO.getGender());
        }
        if (!this.save(user)) {
            log.error("openid={}, 创建用户记录失败", wxCodeSession.getOpenId());
            // 保存失败则抛出异常
            throw new UserException("创建用户记录失败");
        }
        log.info("openid={}, 创建用户记录成功", wxCodeSession.getOpenId());
        user = this.getById(user.getOpenId());
        if (user == null) {
            throw new UserException("用户不存在");
        }
        user.setStatus(AccountStatusEnum.ACTIVE);
        // 更新用户状态到数据库
        if (!this.updateById(user)) {
            log.error("openid={}, 更新用户状态失败", wxCodeSession.getOpenId());
            throw new UserException("更新用户状态失败");
        }
        user = this.getById(user.getOpenId());
        if (user == null) {
            throw new UserException("用户不存在");
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

    @Override
    public ResponseEntity<Result<IPage<UserListVO>>> list(UserListDTO userListDTO) {
        Response<IPage<UserListVO>> response = new Response<>();
        
        // 构建分页参数
        int pageNum = userListDTO.getPageNum() != null ? userListDTO.getPageNum() : 1;
        int pageSize = userListDTO.getPageSize() != null ? userListDTO.getPageSize() : 10;
        Page<User> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 仅查询普通用户，排除商户(is_super=1)
        queryWrapper.and(wrapper -> wrapper.isNull("is_super").or().eq("is_super", 0));
        
        // 昵称模糊查询
        if (StringUtils.hasText(userListDTO.getNickName())) {
            queryWrapper.like("nick_name", userListDTO.getNickName());
        }
        
        // 手机号尾号四位查询
        if (StringUtils.hasText(userListDTO.getPhoneSuffix())) {
            queryWrapper.likeRight("phone", userListDTO.getPhoneSuffix());
        }
        
        // 状态查询
        if (userListDTO.getStatus() != null) {
            queryWrapper.eq("status", userListDTO.getStatus());
        }
        
        // 性别查询
        if (userListDTO.getGender() != null) {
            queryWrapper.eq("gender", userListDTO.getGender());
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc("gmt_created");
        
        // 执行分页查询
        IPage<User> userPage = this.page(page, queryWrapper);
        List<User> users = userPage.getRecords();
        
        // 批量查询用户积分，避免N+1问题
        List<String> openIds = users.stream()
                .map(User::getOpenId)
                .collect(Collectors.toList());
        
        Map<String, UserPoint> userPointMap = Map.of();
        if (!openIds.isEmpty()) {
            List<UserPoint> userPoints = userPointService.listByOpenIds(openIds);
            userPointMap = userPoints.stream()
                    .collect(Collectors.toMap(UserPoint::getOpenId, up -> up, (a, b) -> a));
        }
        
        // 转换为VO
        final Map<String, UserPoint> finalUserPointMap = userPointMap;
        IPage<UserListVO> voPage = userPage.convert(user -> 
                UserListVO.of(user, finalUserPointMap.get(user.getOpenId())));
        
        response.setData(voPage);
        return response.value();
    }

    @Override
    public boolean isMerchant(String openId) {
        User user = this.getById(openId);
        if (user == null) {
            log.warn("用户不存在，openId: {}", openId);
            return false;
        }
        return user.getIsSuper() != null && user.getIsSuper() == 1;
    }

    @Override
    public ResponseEntity<Result<List<UserListVO>>> listByOpenIds(UserOpenIdsDTO userOpenIdsDTO) {
        Response<List<UserListVO>> response = new Response<>();
        List<String> openIds = Optional.ofNullable(userOpenIdsDTO)
                .map(UserOpenIdsDTO::getOpenIds)
                .orElse(List.of());
        if (openIds.isEmpty()) {
            response.setData(List.of());
            return response.value();
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("open_id", openIds);
        List<User> users = this.list(queryWrapper);
        if (users == null || users.isEmpty()) {
            response.setData(List.of());
            return response.value();
        }

        Map<String, UserPoint> userPointMap = userPointService.listByOpenIds(openIds).stream()
                .collect(Collectors.toMap(UserPoint::getOpenId, up -> up, (a, b) -> a));

        List<UserListVO> vos = users.stream()
                .map(user -> UserListVO.of(user, userPointMap.get(user.getOpenId())))
                .toList();
        if (!openIds.isEmpty()) {
            Map<String, Integer> orderMap = openIds.stream()
                    .distinct()
                    .collect(Collectors.toMap(openId -> openId, openId -> openIds.indexOf(openId), (a, b) -> a));
            vos = vos.stream()
                    .sorted((a, b) -> Integer.compare(
                            orderMap.getOrDefault(a.getOpenId(), Integer.MAX_VALUE),
                            orderMap.getOrDefault(b.getOpenId(), Integer.MAX_VALUE)))
                    .toList();
        }

        response.setData(vos);
        return response.value();
    }
}
