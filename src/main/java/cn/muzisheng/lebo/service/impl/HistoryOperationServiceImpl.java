package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.HistoryOperationSelectDTO;
import cn.muzisheng.lebo.entity.HistoryOperation;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.mapper.HistoryOperationMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.HistoryOperationService;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.utils.UserThreadUtil;
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
import java.util.Map;
import java.util.Optional;

/**
 * 历史操作记录服务实现
 */
@Log4j2
@Service
public class HistoryOperationServiceImpl extends ServiceImpl<HistoryOperationMapper, HistoryOperation> implements HistoryOperationService {

    private final UserService userService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 操作类型枚举
     */
    private static final Map<Integer, String> OPERATION_TYPE_DESC = Map.of(
            0, "商品添加",
            1, "商品修改",
            2, "商品删除",
            3, "手动备份数据",
            4, "自动备份数据",
            5, "后台系统登录",
            6, "商品分类添加",
            7, "商品分类修改",
            8, "商品分类删除",
            9, "导入备份数据"
    );

    public HistoryOperationServiceImpl(UserService userService) {
        this.userService = userService;
    }

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
        String operatorName = Optional.ofNullable(operatorId)
                .map(userService::getUserByOpenId)
                .map(User::getNickName)
                .orElse(null);

        // 构建历史操作记录
        HistoryOperation historyOperation = HistoryOperation.builder()
                .type(type)
                .content(content)
                .operatorId(operatorId)
                .operatorName(operatorName)
                .time(LocalDateTime.now())
                .build();

        // 保存记录
        Optional.of(this.save(historyOperation))
                .filter(saved -> saved)
                .orElseThrow(() -> {
                    log.error("添加历史操作记录失败, type: {}, content: {}", type, content);
                    return new GeneralException("添加历史操作记录失败");
                });

        log.info("添加历史操作记录成功, type: {}, operatorName: {}, content: {}",
                OPERATION_TYPE_DESC.get(type), operatorName, content);
    }

    /**
     * 获取历史操作记录列表（分页）
     *
     * @param dto 查询条件DTO
     * @return 分页历史操作记录
     */
    @Override
    public ResponseEntity<Result<IPage<HistoryOperation>>> getHistoryOperation(HistoryOperationSelectDTO dto) {
        Response<IPage<HistoryOperation>> response = new Response<>();

        // 获取分页参数
        int pageNum = Optional.ofNullable(dto)
                .map(HistoryOperationSelectDTO::getPageNum)
                .orElse(1);
        int pageSizeNum = Optional.ofNullable(dto)
                .map(HistoryOperationSelectDTO::getPageSize)
                .orElse(10);

        // 构建查询条件
        QueryWrapper<HistoryOperation> queryWrapper = new QueryWrapper<>();

        Optional.ofNullable(dto).ifPresent(d -> {
            // 操作类型筛选
            Optional.ofNullable(d.getType())
                    .ifPresent(type -> {
                        validateOperationType(type);
                        queryWrapper.eq("type", type);
                    });

            // 时间范围筛选
            Optional.ofNullable(d.getStartTime())
                    .filter(this::isNotBlank)
                    .ifPresent(startTime -> queryWrapper.ge("time", parseDateTime(startTime, "开始时间")));

            Optional.ofNullable(d.getEndTime())
                    .filter(this::isNotBlank)
                    .ifPresent(endTime -> queryWrapper.le("time", parseDateTime(endTime, "结束时间")));

            // 操作内容模糊查询
            Optional.ofNullable(d.getContent())
                    .filter(this::isNotBlank)
                    .ifPresent(content -> queryWrapper.like("content", content));

            // 用户名称模糊查询
            Optional.ofNullable(d.getUserName())
                    .filter(this::isNotBlank)
                    .ifPresent(userName -> queryWrapper.like("operator_name", userName));
        });

        // 按时间倒序排列
        queryWrapper.orderByDesc("time");

        // 分页查询
        Page<HistoryOperation> pageParam = new Page<>(pageNum, pageSizeNum);
        IPage<HistoryOperation> resultPage = this.page(pageParam, queryWrapper);

        response.setData(resultPage);
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
