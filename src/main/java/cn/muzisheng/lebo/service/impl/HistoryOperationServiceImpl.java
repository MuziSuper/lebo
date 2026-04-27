package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.HistoryOperationSelectDTO;
import cn.muzisheng.lebo.entity.HistoryOperation;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.mapper.HistoryOperationMapper;
import cn.muzisheng.lebo.mapper.UserMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.HistoryOperationService;
import cn.muzisheng.lebo.utils.IdUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import cn.muzisheng.lebo.vo.HistoryOperationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class HistoryOperationServiceImpl extends ServiceImpl<HistoryOperationMapper, HistoryOperation> implements HistoryOperationService {

    private final UserMapper userMapper;

    public HistoryOperationServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<Integer, String> OPERATION_TYPE_DESC = Map.of(
            0, "商品添加",
            1, "商品修改",
            2, "商品删除",
            3, "手动备份",
            4, "自动备份",
            5, "系统登录",
            6, "类目添加",
            7, "类目修改",
            8, "类目删除",
            9, "导入备份"
    );


    /**
     * 添加历史操作记录
     *
     * @param type    操作类型，0: 商品添加，1: 商品修改，2: 商品删除，3: 手动备份数据，4: 自动备份数据，
     *                5: 后台系统登录，6: 商品分类添加，7: 商品分类修改，8: 商品分类删除
     * @param content 操作内容
     */
    @Override
    public void addHistoryOperation(Integer type, String content) {
        // 参数校验
        validateOperationType(type);

        // 获取当前操作用户信息
        String operatorId = UserThreadUtil.getCurrentOpenId();

        // 构建历史操作记录
        HistoryOperation historyOperation = HistoryOperation.builder()
                .id(IdUtil.generateHistoryOperationId())
                .type(type)
                .content(content)
                .operatorId(operatorId)
                .time(LocalDateTime.now())
                .build();

        // 保存记录
        Optional.of(this.save(historyOperation))
                .filter(saved -> saved)
                .orElseThrow(() -> {
                    log.error("添加历史操作记录失败, type: {}, content: {}", type, content);
                    return new GeneralException("添加历史操作记录失败");
                });

        log.info("添加历史操作记录成功, type: {}, content: {}",
                OPERATION_TYPE_DESC.get(type),  content);
    }

    @Override
    public ResponseEntity<Result<IPage<HistoryOperationVO>>> getHistoryOperation(HistoryOperationSelectDTO dto) {
        Response<IPage<HistoryOperationVO>> response = new Response<>();

        int pageNum = Optional.ofNullable(dto)
                .map(HistoryOperationSelectDTO::getPageNum)
                .orElse(1);
        int pageSizeNum = Optional.ofNullable(dto)
                .map(HistoryOperationSelectDTO::getPageSize)
                .orElse(10);

        QueryWrapper<HistoryOperation> queryWrapper = new QueryWrapper<>();

        Optional.ofNullable(dto).ifPresent(d -> {
            Optional.ofNullable(d.getType())
                    .ifPresent(type -> {
                        validateOperationType(type);
                        queryWrapper.eq("type", type);
                    });

            Optional.ofNullable(d.getStartTime())
                    .filter(this::isNotBlank)
                    .ifPresent(startTime -> queryWrapper.ge("time", parseDateTime(startTime, "开始时间")));

            Optional.ofNullable(d.getEndTime())
                    .filter(this::isNotBlank)
                    .ifPresent(endTime -> queryWrapper.le("time", parseDateTime(endTime, "结束时间")));

            Optional.ofNullable(d.getContent())
                    .filter(this::isNotBlank)
                    .ifPresent(content -> queryWrapper.like("content", content));
        });

        queryWrapper.orderByDesc("time");

        Page<HistoryOperation> pageParam = new Page<>(pageNum, pageSizeNum);
        IPage<HistoryOperation> resultPage = this.page(pageParam, queryWrapper);

        List<HistoryOperation> records = resultPage.getRecords();
        Set<String> operatorIds = records.stream()
                .map(HistoryOperation::getOperatorId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());

        Map<String, String> userNameMap = Map.of();
        if (!operatorIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(operatorIds);
            userNameMap = users.stream()
                    .collect(Collectors.toMap(User::getOpenId, User::getNickName, (a, b) -> a));
        }

        Map<String, String> finalUserNameMap = userNameMap;
        List<HistoryOperationVO> voList = records.stream()
                .map(op -> {
                    HistoryOperationVO vo = new HistoryOperationVO();
                    vo.setId(op.getId());
                    vo.setContent(op.getContent());
                    vo.setType(op.getType());
                    vo.setOperatorId(op.getOperatorId());
                    vo.setOperatorName(finalUserNameMap.getOrDefault(op.getOperatorId(), ""));
                    vo.setTime(op.getTime());
                    return vo;
                })
                .collect(Collectors.toList());

        if (dto != null && isNotBlank(dto.getUserName())) {
            String userNameFilter = dto.getUserName().trim();
            voList = voList.stream()
                    .filter(vo -> vo.getOperatorName() != null && vo.getOperatorName().contains(userNameFilter))
                    .collect(Collectors.toList());
        }

        Page<HistoryOperationVO> voPage = new Page<>(pageNum, pageSizeNum);
        voPage.setRecords(voList);
        voPage.setTotal(resultPage.getTotal());
        voPage.setSize(resultPage.getSize());
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setPages(resultPage.getPages());

        response.setData(voPage);
        return response.value();
    }

    /**
     * 校验操作类型
     */
    private void validateOperationType(Integer type) {
        Optional.ofNullable(type)
                .filter(OPERATION_TYPE_DESC::containsKey)
                .orElseThrow(() -> {
                    log.error("操作类型不存在或为空, type: {}", type);
                    return new GeneralException("操作类型不存在或为空, type: " + type);
                });
    }

    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr, String fieldName) {
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("{}格式错误, value: {}", fieldName, dateTimeStr);
            throw new GeneralException(fieldName + "格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
    }

    /**
     * 判断字符串是否非空白
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
