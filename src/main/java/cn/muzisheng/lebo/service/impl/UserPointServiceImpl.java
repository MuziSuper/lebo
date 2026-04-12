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
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.PointRecordService;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.utils.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
public class UserPointServiceImpl extends ServiceImpl<UserPointMapper, UserPoint> implements UserPointService {
    
    private final ProductService productService;
    private final UserService userService;
    private final PointRecordService pointRecordService;
    
    public UserPointServiceImpl(ProductService productService,
                                 @Lazy UserService userService,
                                PointRecordService pointRecordService) {
        this.productService = productService;
        this.pointRecordService = pointRecordService;
        this.userService = userService;
    }
    
    /**
     * 创建用户积分钱包
     * @param openid 用户openid
     * @param defaultPoint 默认积分
     * @return 创建的用户积分钱包
     * @throws UserPointException 当用户积分钱包创建失败时抛出
     */
    @Override
    @Transactional
    public UserPoint create(String openid, Long defaultPoint) throws UserPointException {
        UserPoint userPoint = new UserPoint();
        userPoint.setId(IdUtil.generateUserPointId());
        userPoint.setOpenId(openid);
        userPoint.setAccumulatedPoint(defaultPoint);
        userPoint.setCurrentPoint(defaultPoint);
        if (!this.save(userPoint)) {
            log.error("openid={}, 创建用户积分记录失败", openid);
            throw new UserPointException("创建用户积分记录失败");
        }
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);
        return this.getOne(queryWrapper);
    }
    /**
     * 创建用户积分钱包,默认为空
     * @param openid 用户openid
     * @return 创建的用户积分钱包
     * @throws UserPointException 当用户积分钱包创建失败时抛出
     */
    @Override
    @Transactional
    public UserPoint create(String openid) {
        UserPoint userPoint = new UserPoint();
        userPoint.setOpenId(openid);
        userPoint.setAccumulatedPoint(0L);
        userPoint.setCurrentPoint(0L);
        if (!this.save(userPoint)) {
            log.error("openid={}, 创建用户积分记录失败", openid);
            throw new UserPointException("创建用户积分记录失败");
        }
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);
        return this.getOne(queryWrapper);
    }
    /**
     * 获取用户积分钱包当前积分
     * @param openid 用户openid
     * @return 当前积分
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    @Override
    @Transactional(readOnly = true)
    public Long getCurrentPoint(String openid) throws UserPointException {
        UserPoint userPoint = getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        if (userPoint == null) {
            log.error("openid={}, 获取用户积分记录失败", openid);
            throw new UserPointException("获取用户积分记录失败");
        }
        return userPoint.getCurrentPoint();
    }
    /**
     * 获取用户积分钱包累计总积分
     * @param openid 用户openid
     * @return 历史总积分
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    @Transactional(readOnly = true)
    @Override
    public Long getAccumulatedPoint(String openid) throws UserPointException {
        UserPoint userPoint = getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        if (userPoint == null) {
            log.error("openid={}, 获取用户积分记录失败", openid);
            throw new UserPointException("获取用户积分记录失败");
        }
        return userPoint.getAccumulatedPoint();
    }
    /**
     * 获取用户积分钱包积分记录
     * @param openid 用户openid
     * @return 积分记录
     * @throws UserPointException 当用户积分钱包不存在时抛出
     */
    @Override
    public UserPoint getPointRecord(String openid) throws UserPointException {
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openid);
        UserPoint userPoint = this.getOne(queryWrapper);
        if (userPoint == null) {
            log.error("openid={}, 获取用户积分记录失败", openid);
            throw new UserPointException("获取用户积分记录失败");
        }
        return userPoint;
    }
    /**
     * 修改用户积分钱包积分
     * @param openid 用户openid
     * @param point 增减的积分，可为正负，正积分则计入累加总积分
     */
    @Override
    public void updatePoint(String openid, @NonNull Long point, PointRecordTypeEnum pointRecordType) throws UserPointException {

        UserPoint userPoint = this.getOne(new QueryWrapper<UserPoint>().eq("open_id", openid).last("FOR UPDATE"));
        if (userPoint == null) {
            log.error("openid={}, 获取原用户积分记录失败", openid);
            throw new UserPointException("获取原用户积分记录失败");
        }
        long beforeAmount = userPoint.getCurrentPoint();
        long newPoint = userPoint.getCurrentPoint() + point;
        if (newPoint < 0) {
            log.error("openid={}, 原用户积分不足", openid);
            throw new UserPointException("原用户积分不足");
        }
        UpdateWrapper<UserPoint> updateWrapper = new UpdateWrapper<>();
        if (point > 0) {
            long newAccumulatedPoint = userPoint.getAccumulatedPoint() + point;
            updateWrapper.set("accumulated_point", newAccumulatedPoint);
        }
        long newCurrentPoint = userPoint.getCurrentPoint() + point;
        updateWrapper.eq("open_id", openid);
        updateWrapper.set("current_point", newCurrentPoint);
        if (!this.update(updateWrapper)) {
            log.error("openid={}, 更新用户积分记录失败", openid);
            throw new UserPointException("更新用户积分记录失败");
        }
        UserPoint newUserPoint = this.getOne(new QueryWrapper<UserPoint>().eq("open_id", openid));
        if (newUserPoint == null) {
            log.error("openid={}, 获取更新后的用户积分记录失败", openid);
            throw new UserPointException("获取更新后的用户积分记录失败");
        }
        
        String description = buildPointRecordDescription(pointRecordType, point);
        PointRecordAddDTO pointRecordAddDTO = new PointRecordAddDTO();
        pointRecordAddDTO.setOpenId(openid);
        pointRecordAddDTO.setDescription(description);
        pointRecordAddDTO.setChangeAmount(point);
        pointRecordAddDTO.setBeforeAmount(beforeAmount);
        pointRecordAddDTO.setAfterAmount(newCurrentPoint);
        pointRecordService.addPointRecordInternal(pointRecordAddDTO);
        
        log.info("openid={}, 积分更新成功, 变动: {}, 变动前: {}, 变动后: {}, 类型: {}", 
                openid, point, beforeAmount, newCurrentPoint, pointRecordType.getDescription());
        
    }
    
    /**
     * 根据积分记录类型构建描述
     * @param pointRecordType 积分记录类型
     * @param point 积分变动值
     * @return 描述字符串
     */
    private String buildPointRecordDescription(PointRecordTypeEnum pointRecordType, Long point) {
        String action = point > 0 ? "获得" : "消耗";
        String absValue = String.valueOf(Math.abs(point));
        
        return switch (pointRecordType) {
            case ORDER_PAY -> "订单结算" + action + "积分" + absValue + "分";
            case PRODUCT_CONVERT -> "商品兑换" + action + "积分" + absValue + "分";
            case DAY_SIGN_IN -> "每日签到" + action + "积分" + absValue + "分";
            default -> "积分" + action + absValue + "分";
        };
    }
    /**
     * 销毁钱包，假删除
     * @param openid 用户openid
     * @return 是否成功销毁
     */
    @Override
    @Transactional
    public Boolean destroy(String openid) throws UserPointException {
        // 使用逻辑删除而非物理删除
        UpdateWrapper<UserPoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("open_id", openid);
        updateWrapper.set("is_deleted", 1);
        boolean result = this.update(updateWrapper);
        if (!result) {
            log.error("openid={}, 销毁用户积分钱包失败", openid);
            throw new UserPointException("销毁用户积分钱包失败");
        }
        return true;
    }
    
    /**
     * 根据openid列表批量查询用户积分钱包
     * @param openIds openid列表
     * @return 用户积分钱包列表
     */
    @Override
    public List<UserPoint> listByOpenIds(List<String> openIds) {
        if (openIds == null || openIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper<UserPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("open_id", openIds);
        return this.list(queryWrapper);
    }
    
    /**
     * 积分兑换商品
     * @param conversionDTO 积分兑换DTO，包含商品列表和描述
     * @return 是否兑换成功
     */
    @Override
    @Transactional(rollbackFor = UserPointException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> convert(ConversionDTO conversionDTO) {
        Response<Boolean> result = new Response<>();
        // 校验参数
        if (conversionDTO == null || conversionDTO.getItems() == null || conversionDTO.getItems().isEmpty()) {
            log.error("兑换商品列表不能为空");
            throw new UserPointException("兑换商品列表不能为空");
        }
        String openId = conversionDTO.getOpenId();
        if (openId == null || openId.trim().isEmpty()) {
            log.error("用户openid不能为空");
            throw new UserPointException("用户openid不能为空");
        }
        // 校验用户是否存在
        User user = userService.getOne(new QueryWrapper<User>().eq("open_id", openId));
        if (user == null) {
            log.error("用户不存在, openid={}", openId);
            throw new UserPointException("用户不存在, openid=" + openId);
        }
        
        List<ConversionItemDTO> items = conversionDTO.getItems();
        // 获取用户积分钱包
        UserPoint userPoint = this.getOne(new QueryWrapper<UserPoint>().eq("open_id", openId).last("FOR UPDATE"));
        // 校验用户积分钱包是否存在
        if (userPoint == null) {
            log.error("用户积分钱包不存在, openId: {}", openId);
            throw new UserPointException("用户积分钱包不存在");
        }
        // 构建商品出库DTO列表
        List<ProductInOutDTO> productInOutDTOList = items.stream()
                .map(item -> ProductInOutDTO.builder()
                        .productId(item.getProductId())
                        .number(-item.getNumber())
                        .description("积分兑换出库, 用户Id: " + openId)
                        .build())
                .toList();
        // 执行商品出库操作,内部会更新商品库存并创建商品出库记录
        PointRecordAddDTO pointRecordAddDTO = productService.outBatchByPoints(productInOutDTOList, userPoint.getCurrentPoint(), openId);
        // 获取用户积分兑换后的积分
        Long afterAmount = pointRecordAddDTO.getAfterAmount();
        // 更新用户积分钱包
        UpdateWrapper<UserPoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("open_id", openId);
        updateWrapper.set("current_point", afterAmount);
        if (!this.update(updateWrapper)) {
            log.error("扣除用户积分失败, openId: {}", openId);
            throw new UserPointException("扣除用户积分失败");
        }
        // 组装积分记录的描述
        String description = buildPointRecordDescription(PointRecordTypeEnum.PRODUCT_CONVERT, pointRecordAddDTO.getChangeAmount());
        pointRecordAddDTO.setDescription(description);
        // 添加积分变动记录
        pointRecordService.addPointRecordInternal(pointRecordAddDTO);

        log.info("openid={}, 积分更新成功, 变动: {}, 变动前: {}, 变动后: {}, 类型: {}",
                pointRecordAddDTO.getOpenId(), pointRecordAddDTO.getChangeAmount(), pointRecordAddDTO.getBeforeAmount(), afterAmount, PointRecordTypeEnum.PRODUCT_CONVERT);

        result.setData(true);
        return result.value();
    }
}
