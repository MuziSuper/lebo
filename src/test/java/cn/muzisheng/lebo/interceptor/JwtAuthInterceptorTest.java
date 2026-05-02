package cn.muzisheng.lebo.interceptor;

import cn.muzisheng.lebo.exception.AuthorizationException;
import cn.muzisheng.lebo.utils.JwtUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * The interceptor is tested with mock servlet requests so no web server is
 * needed. JwtUtil is mocked because token parsing itself is covered by
 * JwtAndContextTest.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthInterceptorTest {

    @Mock
    private JwtUtil jwtUtil;

    @AfterEach
    void clearThreadContext() {
        UserThreadUtil.removeCurrentOpenId();
    }

    @Test
    void preHandleStoresOpenIdWhenBearerTokenIsValid() throws Exception {
        JwtAuthInterceptor interceptor = new JwtAuthInterceptor(jwtUtil);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/info");
        request.addHeader("Authorization", "Bearer token-value");
        when(jwtUtil.getOpenidFromToken("token-value")).thenReturn("openid-1");

        boolean allowed = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertThat(allowed).isTrue();
        assertThat(UserThreadUtil.getCurrentOpenId()).isEqualTo("openid-1");
        interceptor.afterCompletion(request, new MockHttpServletResponse(), new Object(), null);
        assertThat(UserThreadUtil.getCurrentOpenId()).isNull();
    }

    @Test
    void preHandleRejectsMissingMalformedAndEmptyParsedTokens() {
        JwtAuthInterceptor interceptor = new JwtAuthInterceptor(jwtUtil);
        MockHttpServletRequest missing = new MockHttpServletRequest("GET", "/user/info");
        MockHttpServletRequest malformed = new MockHttpServletRequest("GET", "/user/info");
        malformed.addHeader("Authorization", "token-value");
        MockHttpServletRequest emptyOpenId = new MockHttpServletRequest("GET", "/user/info");
        emptyOpenId.addHeader("Authorization", "Bearer token-value");
        when(jwtUtil.getOpenidFromToken("token-value")).thenReturn(" ");

        assertThatThrownBy(() -> interceptor.preHandle(missing, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("token 不存在");
        assertThatThrownBy(() -> interceptor.preHandle(malformed, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("token 格式不正确");
        assertThatThrownBy(() -> interceptor.preHandle(emptyOpenId, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("token 解析后为空");
    }
}
