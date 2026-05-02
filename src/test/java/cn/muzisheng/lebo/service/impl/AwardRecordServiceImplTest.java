package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.AwardRecord;
import cn.muzisheng.lebo.entity.Information;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.mapper.AwardRecordMapper;
import cn.muzisheng.lebo.model.Awards;
import cn.muzisheng.lebo.model.PointRecordTypeEnum;
import cn.muzisheng.lebo.service.InformationService;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.service.UserSignInService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Lottery tests keep randomness deterministic by configuring a single award
 * with 100% probability. That lets the real draw path run while keeping
 * assertions stable.
 */
@ExtendWith(MockitoExtension.class)
class AwardRecordServiceImplTest {

    @Mock
    private UserSignInService userSignInService;
    @Mock
    private UserPointService userPointService;
    @Mock
    private InformationService informationService;
    @Mock
    private AwardRecordMapper awardRecordMapper;

    private AwardRecordServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AwardRecordServiceImpl(userSignInService, userPointService, informationService);
        ReflectionTestUtils.setField(service, "baseMapper", awardRecordMapper);
        UserThreadUtil.setCurrentOpenId("openid-1");
    }

    @AfterEach
    void tearDown() {
        UserThreadUtil.removeCurrentOpenId();
    }

    @Test
    void updateAwardsAcceptsValidConfigurationAndRejectsInvalidData() {
        List<Awards> valid = List.of(Awards.builder()
                .probability(new BigDecimal("100"))
                .goods("10")
                .isPoint(true)
                .build());

        assertThat(service.updateAwards(valid).getBody().getData()).isTrue();
        assertThat(service.getAwardsList().getBody().getData()).containsExactlyElementsOf(valid);

        assertThatThrownBy(() -> service.updateAwards(List.of()))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("奖项数据不能为空");
        assertThatThrownBy(() -> service.updateAwards(List.of(Awards.builder()
                .probability(new BigDecimal("50"))
                .goods("A")
                .build())))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("概率总和必须等于100");
        assertThatThrownBy(() -> service.updateAwards(List.of(Awards.builder()
                .probability(new BigDecimal("100"))
                .goods("0")
                .isPoint(true)
                .build())))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("积分奖品必须是正整数");
    }

    @Test
    void updateAwardsRejectsMissingNegativeTinyAndNonNumericPointAwards() {
        assertThatThrownBy(() -> service.updateAwards(List.of(Awards.builder()
                .goods("10")
                .build())))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("奖项概率和物品不能为空");
        assertThatThrownBy(() -> service.updateAwards(List.of(Awards.builder()
                .probability(new BigDecimal("-1"))
                .goods("10")
                .build())))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("奖项概率不能为负数");
        assertThatThrownBy(() -> service.updateAwards(List.of(Awards.builder()
                .probability(new BigDecimal("0.00001"))
                .goods("10")
                .build())))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("奖项概率不能低于");
        assertThatThrownBy(() -> service.updateAwards(List.of(Awards.builder()
                .probability(new BigDecimal("100"))
                .goods("not-number")
                .isPoint(true)
                .build())))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("积分奖品的goods必须是正整数");
    }

    @Test
    void drawAwardRequiresSignInAndNoPriorDraw() {
        when(userSignInService.hasSignedToday("openid-1")).thenReturn(false);

        assertThatThrownBy(() -> service.drawAward())
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("请先完成今日签到");

        when(userSignInService.hasSignedToday("openid-1")).thenReturn(true);
        when(awardRecordMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        assertThatThrownBy(() -> service.drawAward())
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining("今日已参与过抽奖");
    }

    @Test
    void drawAwardGrantsPointAwardCreatesNotificationAndSavesRecord() {
        Awards pointAward = Awards.builder()
                .probability(new BigDecimal("100"))
                .goods("30")
                .isPoint(true)
                .build();
        service.updateAwards(List.of(pointAward));
        when(userSignInService.hasSignedToday("openid-1")).thenReturn(true);
        when(awardRecordMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(awardRecordMapper.insert(any(AwardRecord.class))).thenReturn(1);

        var response = service.drawAward();

        assertThat(response.getBody().getData()).isEqualTo(pointAward);
        verify(userPointService).updatePoint("openid-1", 30L, PointRecordTypeEnum.LOTTERY_AWARD);
        ArgumentCaptor<Information> informationCaptor = ArgumentCaptor.forClass(Information.class);
        verify(informationService).saveInformation(informationCaptor.capture());
        assertThat(informationCaptor.getValue().getOpenId()).isEqualTo("openid-1");
        assertThat(informationCaptor.getValue().getSubject()).isEqualTo("抽奖中奖通知");
        ArgumentCaptor<AwardRecord> recordCaptor = ArgumentCaptor.forClass(AwardRecord.class);
        verify(awardRecordMapper).insert(recordCaptor.capture());
        assertThat(recordCaptor.getValue().getAwardGoods()).isEqualTo("30");
    }

    @Test
    void saveAndHasDrawnTodayUseMapperBoundary() {
        when(awardRecordMapper.selectCount(any(Wrapper.class))).thenReturn(1L);
        when(awardRecordMapper.insert(any(AwardRecord.class))).thenReturn(1);

        assertThat(service.hasDrawnToday("openid-1")).isTrue();
        service.saveAwardRecord("openid-1", "咖啡");

        ArgumentCaptor<AwardRecord> captor = ArgumentCaptor.forClass(AwardRecord.class);
        verify(awardRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getUserOpenid()).isEqualTo("openid-1");
        assertThat(captor.getValue().getAwardGoods()).isEqualTo("咖啡");
    }
}
