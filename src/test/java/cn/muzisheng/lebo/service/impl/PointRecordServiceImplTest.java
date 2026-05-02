package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.PointRecordAddDTO;
import cn.muzisheng.lebo.dto.PointRecordListDTO;
import cn.muzisheng.lebo.entity.PointRecord;
import cn.muzisheng.lebo.mapper.PointRecordMapper;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Point-record tests verify both public API-style responses and internal
 * exception behavior. Inserted records are captured from the mapper instead of
 * precomputing fake outcomes.
 */
@ExtendWith(MockitoExtension.class)
class PointRecordServiceImplTest {

    @Mock
    private PointRecordMapper pointRecordMapper;

    private PointRecordServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PointRecordServiceImpl();
        ReflectionTestUtils.setField(service, "baseMapper", pointRecordMapper);
    }

    @AfterEach
    void tearDown() {
        UserThreadUtil.removeCurrentOpenId();
    }

    @Test
    void addPointRecordUsesDtoOpenIdOrThreadContextAndReportsErrors() {
        PointRecordAddDTO dto = recordDto(null, 5L, 10L, 15L);
        UserThreadUtil.setCurrentOpenId("thread-openid");
        when(pointRecordMapper.insert(any(PointRecord.class))).thenReturn(1);

        var response = service.addPointRecord(dto);

        assertThat(response.getBody().getData()).isTrue();
        ArgumentCaptor<PointRecord> captor = ArgumentCaptor.forClass(PointRecord.class);
        verify(pointRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getOpenId()).isEqualTo("thread-openid");
        assertThat(captor.getValue().getChangeAmount()).isEqualTo(5L);

        assertThat(service.addPointRecord(null).getBody().getError()).contains("参数为空");
        UserThreadUtil.removeCurrentOpenId();
        assertThat(service.addPointRecord(recordDto(null, 1L, 0L, 1L)).getBody().getError()).contains("用户openid为空");
    }

    @Test
    void addPointRecordInternalThrowsForInvalidInputAndFailedSave() {
        assertThatThrownBy(() -> service.addPointRecordInternal(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("积分记录参数不能为空");
        assertThatThrownBy(() -> service.addPointRecordInternal(recordDto(" ", 1L, 0L, 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户openid不能为空");

        when(pointRecordMapper.insert(any(PointRecord.class))).thenReturn(0);
        assertThatThrownBy(() -> service.addPointRecordInternal(recordDto("openid-1", 1L, 0L, 1L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("新增积分记录失败");
    }

    @Test
    void getPointRecordByOrderIdReturnsConvertedPage() {
        PointRecord record = new PointRecord();
        record.setId("PR_1");
        record.setOpenId("openid-1");
        record.setOrderId("ORD_1");
        record.setDescription("每日签到获得积分10分");
        record.setChangeAmount(10L);
        record.setBeforeAmount(0L);
        record.setAfterAmount(10L);
        record.setGmtCreated(LocalDateTime.now());
        when(pointRecordMapper.selectPage(any(Page.class), any(Wrapper.class)))
                .thenAnswer(invocation -> {
                    Page<PointRecord> page = invocation.getArgument(0);
                    page.setRecords(List.of(record));
                    page.setTotal(1);
                    return page;
                });
        PointRecordListDTO dto = new PointRecordListDTO();
        dto.setOpenId("openid-1");
        dto.setOrderId("ORD_1");
        dto.setPageNum(1);
        dto.setPageSize(5);

        var page = service.getPointRecordByOrderId(dto).getBody().getData();

        assertThat(page.getTotal()).isEqualTo(1);
        assertThat(page.getRecords()).hasSize(1);
        assertThat(page.getRecords().get(0).getId()).isEqualTo("PR_1");
        assertThat(page.getRecords().get(0).getChangeAmount()).isEqualTo(10);
    }

    private PointRecordAddDTO recordDto(String openId, long change, long before, long after) {
        PointRecordAddDTO dto = new PointRecordAddDTO();
        dto.setOpenId(openId);
        dto.setOrderId("ORD_1");
        dto.setDescription("desc");
        dto.setChangeAmount(change);
        dto.setBeforeAmount(before);
        dto.setAfterAmount(after);
        return dto;
    }
}
