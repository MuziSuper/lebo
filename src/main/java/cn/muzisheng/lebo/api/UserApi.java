
package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.BossLoginDTO;
import cn.muzisheng.lebo.dto.LoginDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.vo.LoginVO;
import cn.muzisheng.lebo.vo.UserUpdateVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 * 提供微信小程序用户的登录、信息查询、注销等功能
 */
@Log4j2
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private UserService userService;


    /**
     * 微信小程序登录接口
     * 通过微信登录 code 换取用户 openid、session_key和unionid, 并更新数据库中该用户的登录信息
     * @param jscode 微信登录凭证（由小程序端调用 wx.login() 获取）
     * @return 是否登录成功
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Boolean>> login(@RequestParam(name = "jscode") String jscode) {
        return userService.login(jscode);
    }

    /**
     * 微信后台登录接口，此接口只提供给商户后台管理网站
     *
     * @param bossLoginDTO 商户后台登录信息
     * @return 是否登录成功
     */
    @PostMapping("/bossLogin")
    public ResponseEntity<Result<Boolean>> bossLogin(@RequestBody BossLoginDTO bossLoginDTO) {
        return userService.bossLogin(bossLoginDTO);
    }
    /**
     * 微信小程序注册接口
     * 通过微信登录 code 换取用户 openid、session_key和unionid，并携带LoginDTO更新数据库中该用户的登录信息
     * @param jscode 微信登录凭证（由小程序端调用 wx.login() 获取）
     * @param loginDTO 用户额外信息（如邀请码等）
     * @return 是否注册成功
     */
    @PostMapping("/register")
    public ResponseEntity<Result<Boolean>> register(@RequestParam(name = "jscode") String jscode, @RequestBody(required = false) LoginDTO loginDTO) {
        return userService.register(jscode, loginDTO);
    }

    /**
     * 更新用户信息,客户端调用后可以再次调用/user/info获取最新数据
     * @param userUpdateVO 用户更新信息
     * @return 更新结果（true/false）
     */
    @PostMapping("/update")
    public ResponseEntity<Result<Boolean>> update(@RequestBody UserUpdateVO userUpdateVO) {
        return userService.update(userUpdateVO);
    }

    /**
     * 获取当前登录用户信息
     * 用户首次登录后，客户端会保存 token，在 token 有效期内再次进入小程序时，
     * 可调用此接口自动获取用户信息，无需重复登录
     *
     * @return 当前登录用户的基本信息（需要有效的 Authorization token）
     */
    @GetMapping("/info")
    public ResponseEntity<Result<LoginVO>> info() {
        return userService.info();
    }

    /**
     * 用户注销接口
     *
     * 服务端更新当前用户的登录状态，客户端接收到true后需要清除保存在缓存中的token，并跳转到登录页面
     *
     * @return 注销结果（true/false）
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Boolean>> logout() {
        return userService.logout();
    }
}