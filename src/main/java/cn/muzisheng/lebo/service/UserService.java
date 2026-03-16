package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.BossLoginDTO;
import cn.muzisheng.lebo.dto.LoginDTO;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.LoginVO;
import cn.muzisheng.lebo.vo.UserUpdateVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

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
}
