package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.AwardRecord;
import cn.muzisheng.lebo.entity.Information;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.mapper.AwardRecordMapper;
import cn.muzisheng.lebo.model.Awards;
import cn.muzisheng.lebo.model.PointRecordTypeEnum;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.AwardRecordService;
import cn.muzisheng.lebo.service.InformationService;
import cn.muzisheng.lebo.service.UserPointService;
import cn.muzisheng.lebo.service.UserSignInService;
import cn.muzisheng.lebo.utils.InformationUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@Service
public class AwardRecordServiceImpl extends ServiceImpl<AwardRecordMapper, AwardRecord> implements AwardRecordService {

    private static final BigDecimal MIN_PROBABILITY = new BigDecimal("0.0001");
    private static final BigDecimal MAX_TOTAL_PROBABILITY = new BigDecimal("100");
    private static final int PROBABILITY_SCALE = 10000;
    private static final String NO_AWARD = "未中奖";

    private final UserSignInService userSignInService;
    private final UserPointService userPointService;
    private final InformationService informationService;
    private final CopyOnWriteArrayList<Awards> awardsList = new CopyOnWriteArrayList<>();
    private volatile int[] probabilityRanges;
    private volatile int totalRange;

    public AwardRecordServiceImpl(UserSignInService userSignInService, UserPointService userPointService, InformationService informationService) {
        this.userSignInService = userSignInService;
        this.userPointService = userPointService;
        this.informationService = informationService;
        // 初始化未中奖奖项
        awardsList.add(Awards.builder()
                .probability(MAX_TOTAL_PROBABILITY)
                .goods(NO_AWARD)
                .build());
        buildProbabilityRanges(awardsList);
    }


    @Override
    public boolean hasDrawnToday(String openId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        QueryWrapper<AwardRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_openid", openId);
        queryWrapper.ge("create_date", startOfDay);
        queryWrapper.lt("create_date", endOfDay);
        
        return this.count(queryWrapper) > 0;
    }

    @Override
    public void saveAwardRecord(String openId, String awardGoods) {
        AwardRecord record = AwardRecord.builder()
                .userOpenid(openId)
                .awardGoods(awardGoods)
                .createDate(LocalDateTime.now())
                .build();
        
        this.save(record);
        log.info("抽奖记录已保存, openId: {}, awardGoods: {}", openId, awardGoods);
    }

    @Override
    public ResponseEntity<Result<Boolean>> updateAwards(List<Awards> awards) {
        Response<Boolean> response = new Response<>();
        
        if (awards == null || awards.isEmpty()) {
            throw new GeneralException("奖项数据不能为空");
        }
        
        BigDecimal totalProbability = BigDecimal.ZERO;
        for (Awards award : awards) {
            if (award.getProbability() == null || award.getGoods() == null) {
                throw new GeneralException("奖项概率和物品不能为空");
            }
            if (award.getProbability().compareTo(BigDecimal.ZERO) < 0) {
                throw new GeneralException("奖项概率不能为负数");
            }
            if (award.getProbability().compareTo(MIN_PROBABILITY) < 0) {
                throw new GeneralException("奖项概率不能低于" + MIN_PROBABILITY + "%");
            }
            if (Boolean.TRUE.equals(award.getIsPoint())) {
                try {
                    int pointValue = Integer.parseInt(award.getGoods());
                    if (pointValue <= 0) {
                        throw new GeneralException("积分奖品必须是正整数");
                    }
                } catch (NumberFormatException e) {
                    throw new GeneralException("积分奖品的goods必须是正整数");
                }
            }
            totalProbability = totalProbability.add(award.getProbability());
        }
        
        if (totalProbability.compareTo(MAX_TOTAL_PROBABILITY) != 0) {
            throw new GeneralException("奖项概率总和必须等于100%，当前总和为：" + totalProbability + "%");
        }
        
        awardsList.clear();
        awardsList.addAll(awards);
        
        buildProbabilityRanges(awards);
        
        log.info("奖项配置已更新，共{}个奖项，概率总和为100%", awards.size());
        
        response.setData(true);
        return response.value();
    }
    
    private void buildProbabilityRanges(List<Awards> awards) {
        int[] ranges = new int[awards.size()];
        int cumulative = 0;
        
        for (int i = 0; i < awards.size(); i++) {
            BigDecimal probability = awards.get(i).getProbability();
            int scaledProbability = probability.multiply(BigDecimal.valueOf(PROBABILITY_SCALE))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
            cumulative += scaledProbability;
            ranges[i] = cumulative;
        }
        
        this.probabilityRanges = ranges;
        Arrays.sort(probabilityRanges);
        this.totalRange = cumulative;
    }

    @Override
    public ResponseEntity<Result<Awards>> drawAward() {
        String openId = UserThreadUtil.getCurrentOpenId();
        Response<Awards> response = new Response<>();
        
        if (!userSignInService.hasSignedToday(openId)) {
            throw new GeneralException("请先完成今日签到后再参与抽奖");
        }
        
        if (hasDrawnToday(openId)) {
            throw new GeneralException("今日已参与过抽奖，请明天再来");
        }
        
        if (awardsList.isEmpty()) {
            throw new GeneralException("暂无奖项配置");
        }
        
        Awards award = performDraw();
        String awardGoods = award.getGoods();
        
        if (Boolean.TRUE.equals(award.getIsPoint())) {
            int pointValue = Integer.parseInt(award.getGoods());
            userPointService.updatePoint(openId, (long) pointValue, PointRecordTypeEnum.LOTTERY_AWARD);
            log.info("用户获得积分奖励, openId: {}, point: {}", openId, pointValue);
        }
        
        if (!NO_AWARD.equals(awardGoods)) {
            createAwardNotification(openId, awardGoods, award.getIsPoint());
        }
        
        saveAwardRecord(openId, awardGoods);
        
        log.info("用户抽奖完成, openId: {}, awardGoods: {}", openId, awardGoods);
        response.setData(award);
        return response.value();
    }
    
    private void createAwardNotification(String openId, String awardGoods,boolean isPoint) {
        String subject = "抽奖中奖通知";
        String content = "";
        if(isPoint){
            content="恭喜您在抽奖活动中获得："+awardGoods+" 积分";
        }else{
            content="恭喜您在抽奖活动中获得："+awardGoods;
        }

        Information notification = InformationUtil.buildPersonalNotification(openId, subject, content);
        informationService.saveInformation(notification);
        
        log.info("抽奖中奖通知已创建, openId: {}, awardGoods: {}", openId, awardGoods);
    }
    
    private Awards performDraw() {
        int randomValue = ThreadLocalRandom.current().nextInt(totalRange);
        
        List<Awards> currentAwards = new ArrayList<>(awardsList);
        int[] currentRanges = probabilityRanges;
        
        for (int i = 0; i < currentRanges.length; i++) {
            if (randomValue < currentRanges[i]) {
                return currentAwards.get(i);
            }
        }
        
        return currentAwards.get(currentAwards.size() - 1);
    }

    @Override
    public ResponseEntity<Result<List<Awards>>> getAwardsList() {
        Response<List<Awards>> response = new Response<>();
        List<Awards> currentAwards = new ArrayList<>(awardsList);
        response.setData(currentAwards);
        return response.value();
    }
}
