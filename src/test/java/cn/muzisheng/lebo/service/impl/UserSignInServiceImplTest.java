package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.UserSignIn;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.mapper.UserSignInMapper;
import cn.muzisheng.lebo.model.PointRecordTypeEnum;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Sign-in tests execute the real service logic. MyBatis is mocked at the mapper
 * boundary so the assertions are about actual branch behavior, not database
 * availability.
 */
@ExtendWith(MockitoExtension.class)
class UserSignInServiceImplTest {

    @Mock
    private UserPointService userPointService;
    @Mock
    private UserSignInMapper userSignInMapper;

    private UserSignInServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserSignInServiceImpl(userPointService);
        ReflectionTestUtils.setField(service, "baseMapper", userSignInMapper);
        UserThreadUtil.setCurrentOpenId("openid-1");
    }

    @AfterEach
    void tearDown() {
        UserThreadUtil.removeCurrentOpenId();
    }

    @Test
    void firstSignInCreatesRecordAndAddsBasePoints() {
        when(userSignInMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        when(userSignInMapper.insert(any(UserSignIn.class))).thenReturn(1);

        var response = service.sign();

        assertThat(response.getBody().getData()).isTrue();
        ArgumentCaptor<UserSignIn> captor = ArgumentCaptor.forClass(UserSignIn.class);
        verify(userSignInMapper).insert(captor.capture());
        assertThat(captor.getValue().getOpenId()).isEqualTo("openid-1");
        assertThat(captor.getValue().getLastSignDate()).isEqualTo(LocalDate.now());
        assertThat(captor.getValue().getContinuousDays()).isEqualTo(1);
        verify(userPointService).updatePoint("openid-1", 10L, PointRecordTypeEnum.DAY_SIGN_IN);
    }

    @Test
    void continuousThirdDaySignInAddsBonusPoints() {
        UserSignIn existing = new UserSignIn();
        existing.setOpenId("openid-1");
        existing.setLastSignDate(LocalDate.now().minusDays(1));
        existing.setContinuousDays(2);
        when(userSignInMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(existing);
        when(userSignInMapper.updateById(existing)).thenReturn(1);

        var response = service.sign();

        assertThat(response.getBody().getData()).isTrue();
        assertThat(existing.getLastSignDate()).isEqualTo(LocalDate.now());
        assertThat(existing.getContinuousDays()).isEqualTo(3);
        verify(userPointService).updatePoint("openid-1", 20L, PointRecordTypeEnum.DAY_SIGN_IN);
    }

    @Test
    void signInRejectsMissingUserAndDuplicateToday() {
        UserThreadUtil.removeCurrentOpenId();
        assertThatThrownBy(() -> service.sign())
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("未获取到当前用户信息");

        UserThreadUtil.setCurrentOpenId("openid-1");
        UserSignIn signedToday = new UserSignIn();
        signedToday.setLastSignDate(LocalDate.now());
        signedToday.setContinuousDays(1);
        when(userSignInMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(signedToday);

        assertThatThrownBy(() -> service.sign())
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("今日已签到");
    }

    @Test
    void todayStatusAndHasSignedTodayReflectStoredRecord() {
        UserSignIn signedToday = new UserSignIn();
        signedToday.setLastSignDate(LocalDate.now());
        when(userSignInMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(signedToday);

        assertThat(service.getTodaySignInStatus().getBody().getData()).isTrue();
        assertThat(service.hasSignedToday("openid-1")).isTrue();

        UserThreadUtil.removeCurrentOpenId();
        assertThat(service.getTodaySignInStatus().getBody().getData()).isFalse();
        assertThat(service.hasSignedToday(null)).isFalse();
    }
}
