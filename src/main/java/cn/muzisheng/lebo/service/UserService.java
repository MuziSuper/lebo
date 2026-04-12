package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.BossLoginDTO;
import cn.muzisheng.lebo.dto.LoginDTO;
import cn.muzisheng.lebo.dto.UserListDTO;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.LoginVO;
import cn.muzisheng.lebo.vo.UserListVO;
import cn.muzisheng.lebo.vo.UserUpdateVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    ResponseEntity<Result<Boolean>> login(String code);
    ResponseEntity<Result<Boolean>> bossLogin(BossLoginDTO bossLoginDTO);
    ResponseEntity<Result<Boolean>> register(String code, LoginDTO loginDTO);
    ResponseEntity<Result<Boolean>> update(UserUpdateVO userUpdateVO);

    ResponseEntity<Result<LoginVO>> info();
    ResponseEntity<Result<Boolean>> logout();
    void updateLastLogin(String openid);

    User getUserByOpenId(String openid);

    /**
     * 分页查询用户列表
     * @param userListDTO 查询条件
     * @return 分页用户列表
     */
    ResponseEntity<Result<IPage<UserListVO>>> list(UserListDTO userListDTO);

    /**
     * 判断用户是否为商户
     * @param openId 用户openId
     * @return true-商户，false-普通用户
     */
    boolean isMerchant(String openId);
    /**
     * 通过用户openid列表查询用户信息
     * @param openIds 用户openid列表
     * @return 用户列表
     */
    ResponseEntity<Result<List<UserListVO>>> listByOpenIds(List<String> openIds);
}
