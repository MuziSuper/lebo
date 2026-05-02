package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.ConversionDTO;
import cn.muzisheng.lebo.dto.ConversionItemDTO;
import cn.muzisheng.lebo.dto.PointRecordAddDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.mapper.UserPointMapper;
import cn.muzisheng.lebo.model.PointRecordTypeEnum;
import cn.muzisheng.lebo.service.PointRecordService;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.lebo.service.UserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User point tests validate wallet mutation rules without a real database. The
 * mapper is mocked, while the service computes balances and point-record DTOs
 * using production code.
 */
@ExtendWith(MockitoExtension.class)
class UserPointServiceImplTest {

    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;
    @Mock
    private PointRecordService pointRecordService;
    @Mock
    private UserPointMapper userPointMapper;

    private UserPointServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserPointServiceImpl(productService, userService, pointRecordService);
        ReflectionTestUtils.setField(service, "baseMapper", userPointMapper);
    }

    @Test
    void createWalletPersistsDefaultPointsAndReturnsStoredWallet() {
        UserPoint stored = wallet("openid-1", 50L, 50L);
        when(userPointMapper.insert(any(UserPoint.class))).thenReturn(1);
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(stored);

        UserPoint result = service.create("openid-1", 50L);

        assertThat(result).isSameAs(stored);
        ArgumentCaptor<UserPoint> captor = ArgumentCaptor.forClass(UserPoint.class);
        verify(userPointMapper).insert(captor.capture());
        assertThat(captor.getValue().getOpenId()).isEqualTo("openid-1");
        assertThat(captor.getValue().getCurrentPoint()).isEqualTo(50L);
        assertThat(captor.getValue().getAccumulatedPoint()).isEqualTo(50L);
    }

    @Test
    void createWalletWithoutDefaultPointUsesZeroBalance() {
        UserPoint stored = wallet("openid-zero", 0L, 0L);
        when(userPointMapper.insert(any(UserPoint.class))).thenReturn(1);
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(stored);

        UserPoint result = service.create("openid-zero");

        assertThat(result).isSameAs(stored);
        ArgumentCaptor<UserPoint> captor = ArgumentCaptor.forClass(UserPoint.class);
        verify(userPointMapper).insert(captor.capture());
        assertThat(captor.getValue().getOpenId()).isEqualTo("openid-zero");
        assertThat(captor.getValue().getCurrentPoint()).isZero();
        assertThat(captor.getValue().getAccumulatedPoint()).isZero();
    }

    @Test
    void createWalletReportsPersistenceFailures() {
        when(userPointMapper.insert(any(UserPoint.class))).thenReturn(0);

        assertThatThrownBy(() -> service.create("openid-1", 10L))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("创建用户积分记录失败");
        assertThatThrownBy(() -> service.create("openid-2"))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("创建用户积分记录失败");
    }

    @Test
    void gettersReturnWalletAmountsAndRejectMissingWallet() {
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(wallet("openid-1", 30L, 80L));

        assertThat(service.getCurrentPoint("openid-1")).isEqualTo(30L);
        assertThat(service.getAccumulatedPoint("openid-1")).isEqualTo(80L);
        assertThat(service.getPointRecord("openid-1").getOpenId()).isEqualTo("openid-1");

        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        assertThatThrownBy(() -> service.getCurrentPoint("missing"))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("获取用户积分记录失败");
    }

    @Test
    void accumulatedAndPointRecordRejectMissingWallet() {
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);

        assertThatThrownBy(() -> service.getAccumulatedPoint("missing"))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("获取用户积分记录失败");
        assertThatThrownBy(() -> service.getPointRecord("missing"))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("获取用户积分记录失败");
    }

    @Test
    void updatePointChangesBalanceAndCreatesInternalRecord() {
        UserPoint before = wallet("openid-1", 100L, 200L);
        UserPoint after = wallet("openid-1", 115L, 215L);
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(before, after);
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(1);

        service.updatePoint("openid-1", 15L, PointRecordTypeEnum.DAY_SIGN_IN);

        ArgumentCaptor<PointRecordAddDTO> captor = ArgumentCaptor.forClass(PointRecordAddDTO.class);
        verify(pointRecordService).addPointRecordInternal(captor.capture());
        assertThat(captor.getValue().getOpenId()).isEqualTo("openid-1");
        assertThat(captor.getValue().getChangeAmount()).isEqualTo(15L);
        assertThat(captor.getValue().getBeforeAmount()).isEqualTo(100L);
        assertThat(captor.getValue().getAfterAmount()).isEqualTo(115L);
        assertThat(captor.getValue().getDescription()).isEqualTo("每日签到获得积分15分");
    }

    @Test
    void updatePointRejectsMissingWalletInsufficientBalanceAndFailedUpdate() {
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        assertThatThrownBy(() -> service.updatePoint("openid-1", 1L, PointRecordTypeEnum.ORDER_PAY))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("获取原用户积分记录失败");

        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(wallet("openid-1", 5L, 5L));
        assertThatThrownBy(() -> service.updatePoint("openid-1", -10L, PointRecordTypeEnum.PRODUCT_CONVERT))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("原用户积分不足");

        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(wallet("openid-1", 5L, 5L));
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(0);
        assertThatThrownBy(() -> service.updatePoint("openid-1", 1L, PointRecordTypeEnum.ORDER_PAY))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("更新用户积分记录失败");
    }

    @Test
    void updatePointRejectsMissingWalletAfterPersistenceAndUsesDefaultDescription() {
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true)))
                .thenReturn(wallet("openid-1", 10L, 20L), null);
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(1);

        assertThatThrownBy(() -> service.updatePoint("openid-1", 5L, PointRecordTypeEnum.LOTTERY_AWARD))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("获取更新后的用户积分记录失败");

        when(userPointMapper.selectOne(any(Wrapper.class), eq(true)))
                .thenReturn(wallet("openid-2", 10L, 20L), wallet("openid-2", 15L, 25L));
        service.updatePoint("openid-2", 5L, PointRecordTypeEnum.LOTTERY_AWARD);

        ArgumentCaptor<PointRecordAddDTO> captor = ArgumentCaptor.forClass(PointRecordAddDTO.class);
        verify(pointRecordService).addPointRecordInternal(captor.capture());
        assertThat(captor.getValue().getDescription()).isEqualTo("积分获得5分");
    }

    @Test
    void destroyAndListByOpenIdsUseMapperResults() {
        List<UserPoint> wallets = List.of(wallet("a", 1L, 1L), wallet("b", 2L, 2L));
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(1);
        when(userPointMapper.selectList(any(Wrapper.class))).thenReturn(wallets);

        assertThat(service.destroy("openid-1")).isTrue();
        assertThat(service.listByOpenIds(List.of("a", "b"))).containsExactlyElementsOf(wallets);
        assertThat(service.listByOpenIds(List.of())).isEmpty();
        assertThat(service.listByOpenIds(null)).isEmpty();
    }

    @Test
    void destroyReportsFailedLogicalDelete() {
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(0);

        assertThatThrownBy(() -> service.destroy("openid-1"))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("销毁用户积分钱包失败");
    }

    @Test
    void convertDeductsPointsAfterProductOutBatch() {
        ConversionItemDTO item = new ConversionItemDTO();
        item.setProductId("p1");
        item.setNumber(2L);
        ConversionDTO dto = new ConversionDTO();
        dto.setOpenId("openid-1");
        dto.setItems(List.of(item));
        PointRecordAddDTO record = new PointRecordAddDTO();
        record.setOpenId("openid-1");
        record.setChangeAmount(-40L);
        record.setBeforeAmount(100L);
        record.setAfterAmount(60L);
        when(userService.getOne(any(Wrapper.class))).thenReturn(new User());
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(wallet("openid-1", 100L, 100L));
        when(productService.outBatchByPoints(any(), eq(100L), eq("openid-1"))).thenReturn(record);
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(1);

        assertThat(service.convert(dto).getBody().getData()).isTrue();

        ArgumentCaptor<List<ProductInOutDTO>> outCaptor = ArgumentCaptor.forClass(List.class);
        verify(productService).outBatchByPoints(outCaptor.capture(), eq(100L), eq("openid-1"));
        assertThat(outCaptor.getValue()).hasSize(1);
        assertThat(outCaptor.getValue().get(0).getProductId()).isEqualTo("p1");
        assertThat(outCaptor.getValue().get(0).getNumber()).isEqualTo(-2L);
        ArgumentCaptor<PointRecordAddDTO> recordCaptor = ArgumentCaptor.forClass(PointRecordAddDTO.class);
        verify(pointRecordService).addPointRecordInternal(recordCaptor.capture());
        assertThat(recordCaptor.getValue().getDescription()).isEqualTo("商品兑换消耗积分40分");
    }

    @Test
    void convertRejectsInvalidInputAndMissingUserOrWallet() {
        assertThatThrownBy(() -> service.convert(null))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("兑换商品列表不能为空");

        ConversionDTO blankOpenId = new ConversionDTO();
        blankOpenId.setItems(List.of(new ConversionItemDTO()));
        blankOpenId.setOpenId(" ");
        assertThatThrownBy(() -> service.convert(blankOpenId))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("用户openid不能为空");

        ConversionDTO dto = new ConversionDTO();
        dto.setItems(List.of(new ConversionItemDTO()));
        dto.setOpenId("missing");
        when(userService.getOne(any(Wrapper.class))).thenReturn(null);
        assertThatThrownBy(() -> service.convert(dto))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("用户不存在");

        when(userService.getOne(any(Wrapper.class))).thenReturn(new User());
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        assertThatThrownBy(() -> service.convert(dto))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("用户积分钱包不存在");
    }

    @Test
    void convertReportsWalletDeductionFailure() {
        ConversionItemDTO item = new ConversionItemDTO();
        item.setProductId("p1");
        item.setNumber(1L);
        ConversionDTO dto = new ConversionDTO();
        dto.setOpenId("openid-1");
        dto.setItems(List.of(item));
        PointRecordAddDTO record = new PointRecordAddDTO();
        record.setOpenId("openid-1");
        record.setChangeAmount(-10L);
        record.setBeforeAmount(20L);
        record.setAfterAmount(10L);
        when(userService.getOne(any(Wrapper.class))).thenReturn(new User());
        when(userPointMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(wallet("openid-1", 20L, 20L));
        when(productService.outBatchByPoints(any(), eq(20L), eq("openid-1"))).thenReturn(record);
        when(userPointMapper.update(eq(null), any(Wrapper.class))).thenReturn(0);

        assertThatThrownBy(() -> service.convert(dto))
                .isInstanceOf(UserPointException.class)
                .hasMessageContaining("扣除用户积分失败");
    }

    private UserPoint wallet(String openId, long current, long accumulated) {
        UserPoint point = new UserPoint();
        point.setOpenId(openId);
        point.setCurrentPoint(current);
        point.setAccumulatedPoint(accumulated);
        return point;
    }
}
