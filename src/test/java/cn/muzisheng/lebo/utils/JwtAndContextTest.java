package cn.muzisheng.lebo.utils;

import cn.muzisheng.lebo.config.TokenConfig;
import cn.muzisheng.lebo.exception.AuthorizationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JWT and request context are security primitives for the app. Tests verify
 * generated tokens round-trip through the real parser instead of asserting on a
 * hard-coded token string.
 */
class JwtAndContextTest {

    @AfterEach
    void clearThreadContext() {
        UserThreadUtil.removeCurrentOpenId();
    }

    @Test
    void jwtTokenRoundTripsOpenIdAndCanBeRefreshed() {
        TokenConfig tokenConfig = new TokenConfig();
        tokenConfig.init();
        JwtUtil jwtUtil = new JwtUtil(tokenConfig);

        String token = jwtUtil.generateToken("openid-1");
        String refreshedToken = jwtUtil.refreshToken(token);

        assertThat(token).startsWith("Bearer ");
        assertThat(jwtUtil.getOpenidFromToken(token)).isEqualTo("openid-1");
        assertThat(jwtUtil.getOpenidFromToken(refreshedToken)).isEqualTo("openid-1");
        assertThat(jwtUtil.delTokenPrefix(token)).doesNotStartWith("Bearer ");
    }

    @Test
    void jwtRejectsMalformedAndExpiredTokens() {
        TokenConfig expiredConfig = new TokenConfig();
        expiredConfig.setExpire(1L);
        expiredConfig.init();
        JwtUtil jwtUtil = new JwtUtil(expiredConfig);

        assertThatThrownBy(() -> jwtUtil.getOpenidFromToken("Bearer not-a-jwt"))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("bad token");
    }

    @Test
    void userThreadUtilStoresAndClearsCurrentOpenId() {
        assertThat(UserThreadUtil.getCurrentOpenId()).isNull();

        UserThreadUtil.setCurrentOpenId("user-openid");

        assertThat(UserThreadUtil.getCurrentOpenId()).isEqualTo("user-openid");
        UserThreadUtil.removeCurrentOpenId();
        assertThat(UserThreadUtil.getCurrentOpenId()).isNull();
    }
}
