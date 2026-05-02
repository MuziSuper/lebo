package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.BossLoginDTO;
import cn.muzisheng.lebo.dto.LoginDTO;
import cn.muzisheng.lebo.dto.UserListDTO;
import cn.muzisheng.lebo.dto.UserOpenIdsDTO;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.vo.UserUpdateVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

/**
 * Verifies UserApi passes real request parameters/objects to UserService.
 * Response correctness is covered in UserService tests, not by pre-stubbing a
 * controller return value here.
 */
class UserApiTest extends UnitTestSupport {

    @Mock
    private UserService userService;

    @Test
    void delegatesAllUserEndpoints() {
        UserApi api = new UserApi();
        ReflectionTestUtils.setField(api, "userService", userService);
        BossLoginDTO bossLoginDTO = new BossLoginDTO();
        LoginDTO loginDTO = new LoginDTO();
        UserUpdateVO updateVO = UserUpdateVO.builder().nickName("new-name").build();
        UserListDTO listDTO = new UserListDTO();
        UserOpenIdsDTO openIdsDTO = new UserOpenIdsDTO();
        api.login("code");
        api.bossLogin(bossLoginDTO);
        api.register("code", loginDTO);
        api.update(updateVO);
        api.info();
        api.logout();
        api.list(listDTO);
        api.listByOpenIds(openIdsDTO);

        verify(userService).login("code");
        verify(userService).bossLogin(bossLoginDTO);
        verify(userService).register("code", loginDTO);
        verify(userService).update(updateVO);
        verify(userService).info();
        verify(userService).logout();
        verify(userService).list(listDTO);
        verify(userService).listByOpenIds(openIdsDTO);
    }
}
