package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.InformationDeleteDTO;
import cn.muzisheng.lebo.dto.MessageBossListDTO;
import cn.muzisheng.lebo.dto.MessageListDTO;
import cn.muzisheng.lebo.dto.SendMessageDTO;
import cn.muzisheng.lebo.entity.Information;
import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.exception.InformationException;
import cn.muzisheng.lebo.mapper.InformationMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.InformationService;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.utils.IdUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import cn.muzisheng.lebo.vo.InformationBossVO;
import cn.muzisheng.lebo.vo.InformationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information> implements InformationService {

    private final UserService userService;

    public InformationServiceImpl(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = InformationException.class)
    public ResponseEntity<Result<Boolean>> sendMessage(SendMessageDTO sendMessageDTO) {
        Response<Boolean> result = new Response<>();
        
        if (sendMessageDTO == null) {
            log.error("消息内容不能为空");
            throw new InformationException("消息内容不能为空");
        }
        
        if (sendMessageDTO.getSubject() == null || sendMessageDTO.getSubject().trim().isEmpty()) {
            log.error("消息主题不能为空");
            throw new InformationException("消息主题不能为空");
        }
        
        if (sendMessageDTO.getContent() == null || sendMessageDTO.getContent().trim().isEmpty()) {
            log.error("消息内容不能为空");
            throw new InformationException("消息内容不能为空");
        }
        
        if (sendMessageDTO.getType() == null || sendMessageDTO.getType() < 0 || sendMessageDTO.getType() > 2) {
            log.error("消息类型不合法, type: {}", sendMessageDTO.getType());
            throw new InformationException("消息类型不合法，必须为0-通知消息，1-系统消息，2-个人消息");
        }
        
        String informationId = IdUtil.generateInformationId();
        List<String> openIds = sendMessageDTO.getOpenIds();
        
        if (openIds == null || openIds.isEmpty()) {
            openIds = getAllCustomerOpenIds();
            log.info("发送消息给所有客户, 数量: {}", openIds.size());
        }
        
        List<Information> informationList = new ArrayList<>();
        for (String openId : openIds) {
            User user = userService.getUserByOpenId(openId);
            String name = user != null ? user.getNickName() : null;
            
            Information information = Information.builder()
                    .id(IdUtil.generateId())
                    .informationId(informationId)
                    .openId(openId)
                    .name(name)
                    .subject(sendMessageDTO.getSubject())
                    .content(sendMessageDTO.getContent())
                    .type(sendMessageDTO.getType())
                    .deleted(0)
                    .isLook(false)
                    .build();
            informationList.add(information);
        }
        
        this.saveBatch(informationList);
        log.info("消息发送成功, informationId: {}, 发送数量: {}", informationId, informationList.size());
        
        result.setData(true);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<IPage<InformationVO>>> messageList(MessageListDTO messageListDTO) {
        Response<IPage<InformationVO>> result = new Response<>();
        
        String openId = UserThreadUtil.getCurrentOpenId();
        if (openId == null || openId.isEmpty()) {
            log.error("用户未登录");
            throw new InformationException("用户未登录");
        }
        
        int pageNum = messageListDTO != null && messageListDTO.getPageNum() != null ? messageListDTO.getPageNum() : 1;
        int pageSize = messageListDTO != null && messageListDTO.getPageSize() != null ? messageListDTO.getPageSize() : 10;
        
        Page<Information> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Information> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openId);
        queryWrapper.eq("is_deleted", 0);
        
        if (messageListDTO != null && messageListDTO.getType() != null) {
            queryWrapper.eq("type", messageListDTO.getType());
        }

        if (messageListDTO != null && messageListDTO.getIsLook() != null) {
            queryWrapper.eq("is_look", messageListDTO.getIsLook());
        }
        
        queryWrapper.orderByDesc("gmt_created");
        
        IPage<Information> informationPage = this.page(page, queryWrapper);
        
        IPage<InformationVO> voPage = informationPage.convert(this::convertToVO);
        
        result.setData(voPage);
        return result.value();
    }

    @Override
    @Transactional(rollbackFor = InformationException.class)
    public ResponseEntity<Result<Boolean>> delete(InformationDeleteDTO informationDeleteDTO) {
        Response<Boolean> result = new Response<>();
        String informationId = informationDeleteDTO == null ? null : informationDeleteDTO.getInformationId();
        if (informationId == null || informationId.trim().isEmpty()) {
            log.error("消息批次ID不能为空");
            throw new InformationException("消息批次ID不能为空");
        }

        QueryWrapper<Information> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("information_id", informationId);
        if (!this.remove(queryWrapper)) {
            log.error("删除消息失败, informationId: {}", informationId);
            throw new InformationException("删除消息失败");
        }
        result.setData(true);
        return result.value();
    }

    @Override
    @Transactional(rollbackFor = InformationException.class)
    public ResponseEntity<Result<String>> look(String id) {
        Response<String> result = new Response<>();
        if (id == null || id.trim().isEmpty()) {
            log.error("消息ID不能为空");
            throw new InformationException("消息ID不能为空");
        }

        String openId = UserThreadUtil.getCurrentOpenId();
        if (openId == null || openId.isEmpty()) {
            log.error("用户未登录");
            throw new InformationException("用户未登录");
        }

        QueryWrapper<Information> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("open_id", openId);
        Information information = this.getOne(queryWrapper);
        if (information == null) {
            log.error("消息不存在或无权限查阅, id: {}, openId: {}", id, openId);
            throw new InformationException("消息不存在或无权限查阅");
        }

        if (Boolean.TRUE.equals(information.getIsLook())) {
            result.setData(id);
            return result.value();
        }

        UpdateWrapper<Information> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id)
                .eq("open_id", openId)
                .set("is_look", true);
        if (!this.update(updateWrapper)) {
            log.error("消息查阅状态更新失败, id: {}, openId: {}", id, openId);
            throw new InformationException("消息查阅状态更新失败");
        }
        result.setData(id);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<IPage<InformationBossVO>>> bossMessageList(MessageBossListDTO messageBossListDTO) {
        Response<IPage<InformationBossVO>> result = new Response<>();
        
        int pageNum = messageBossListDTO != null && messageBossListDTO.getPageNum() != null ? messageBossListDTO.getPageNum() : 1;
        int pageSize = messageBossListDTO != null && messageBossListDTO.getPageSize() != null ? messageBossListDTO.getPageSize() : 10;
        
        QueryWrapper<Information> countWrapper = new QueryWrapper<>();
        QueryWrapper<Information> idWrapper = new QueryWrapper<>();
        
        if (messageBossListDTO != null) {
            if (messageBossListDTO.getType() != null) {
                countWrapper.eq("type", messageBossListDTO.getType());
                idWrapper.eq("type", messageBossListDTO.getType());
            }
            
            if (messageBossListDTO.getLikeSubject() != null && !messageBossListDTO.getLikeSubject().trim().isEmpty()) {
                countWrapper.like("subject", messageBossListDTO.getLikeSubject());
                idWrapper.like("subject", messageBossListDTO.getLikeSubject());
            }
            
            if (messageBossListDTO.getStartGmtCreated() != null && messageBossListDTO.getEndGmtCreated() != null) {
                countWrapper.between("gmt_created", messageBossListDTO.getStartGmtCreated(), messageBossListDTO.getEndGmtCreated());
                idWrapper.between("gmt_created", messageBossListDTO.getStartGmtCreated(), messageBossListDTO.getEndGmtCreated());
            } else if (messageBossListDTO.getStartGmtCreated() != null) {
                countWrapper.ge("gmt_created", messageBossListDTO.getStartGmtCreated());
                idWrapper.ge("gmt_created", messageBossListDTO.getStartGmtCreated());
            } else if (messageBossListDTO.getEndGmtCreated() != null) {
                countWrapper.le("gmt_created", messageBossListDTO.getEndGmtCreated());
                idWrapper.le("gmt_created", messageBossListDTO.getEndGmtCreated());
            }
            
            if (messageBossListDTO.getDeleted() != null) {
                countWrapper.eq("is_deleted", messageBossListDTO.getDeleted());
                idWrapper.eq("is_deleted", messageBossListDTO.getDeleted());
            }
        }
        
        countWrapper.select("COUNT(DISTINCT information_id) as total");
        List<Map<String, Object>> countResult = this.getBaseMapper().selectMaps(countWrapper);
        long total = countResult.isEmpty() ? 0L : ((Number) countResult.get(0).get("total")).longValue();
        
        idWrapper.select("MIN(id) as id", "MAX(gmt_created) as gmt_created").groupBy("information_id").orderByDesc("gmt_created");
        
        List<Map<String, Object>> idRecords = this.getBaseMapper().selectMaps(idWrapper);
        List<String> ids = idRecords.stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .map(record -> (String) record.get("id"))
                .toList();
        
        List<Information> informationList = new ArrayList<>();
        if (!ids.isEmpty()) {
            QueryWrapper<Information> dataWrapper = new QueryWrapper<>();
            dataWrapper.in("id", ids).orderByDesc("gmt_created");
            informationList = this.list(dataWrapper);
        }
        
        List<String> informationIds = informationList.stream()
                .map(Information::getInformationId)
                .toList();
        
        Map<String, List<String>> informationOpenIdsMap = new HashMap<>();
        if (!informationIds.isEmpty()) {
            QueryWrapper<Information> openIdQueryWrapper = new QueryWrapper<>();
            openIdQueryWrapper.in("information_id", informationIds);
            openIdQueryWrapper.select("information_id", "open_id");
            List<Information> allInformationList = this.list(openIdQueryWrapper);
            
            for (Information info : allInformationList) {
                informationOpenIdsMap.computeIfAbsent(info.getInformationId(), k -> new ArrayList<>())
                        .add(info.getOpenId());
            }
        }
        
        List<InformationBossVO> voList = informationList.stream()
                .map(info -> convertToBossVO(info, informationOpenIdsMap))
                .toList();
        
        Page<InformationBossVO> voPage = new Page<>(pageNum, pageSize, total);
        voPage.setRecords(voList);
        
        result.setData(voPage);
        return result.value();
    }

    /**
     * 保存单条消息记录
     * @param information 消息实体
     * @return 是否保存成功
     */
    @Override
    public boolean saveInformation(Information information) {
        return this.save(information);
    }
    
    private List<String> getAllCustomerOpenIds() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_super", 0);
        queryWrapper.eq("is_deleted", 0);
        List<User> users = userService.list(queryWrapper);
        return users.stream().map(User::getOpenId).toList();
    }
    
    private InformationVO convertToVO(Information information) {
        InformationVO vo = new InformationVO();
        vo.setId(information.getId());
        vo.setType(information.getType());
        vo.setSubject(information.getSubject());
        vo.setContent(information.getContent());
        vo.setIsLook(information.getIsLook());
        vo.setGmtCreated(information.getGmtCreated());
        return vo;
    }
    
    private InformationBossVO convertToBossVO(Information information, Map<String, List<String>> informationOpenIdsMap) {
        InformationBossVO vo = new InformationBossVO();
        vo.setId(information.getId());
        vo.setInformationId(information.getInformationId());
        vo.setType(information.getType());
        vo.setSubject(information.getSubject());
        vo.setContent(information.getContent());
        vo.setGmtCreated(information.getGmtCreated());
        vo.setDeleted(information.getDeleted());
        vo.setOpenIds(informationOpenIdsMap.getOrDefault(information.getInformationId(), new ArrayList<>()));
        return vo;
    }
}
