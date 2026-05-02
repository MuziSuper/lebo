package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.service.UserSignInService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class UserSignInApiTest extends UnitTestSupport {

    @Mock
    private UserSignInService userSignInService;

    @Test
    void delegatesSignInEndpoints() {
        UserSignInApi api = new UserSignInApi(userSignInService);
        api.sign();
        api.getTodaySignInStatus();

        verify(userSignInService).sign();
        verify(userSignInService).getTodaySignInStatus();
    }
}
