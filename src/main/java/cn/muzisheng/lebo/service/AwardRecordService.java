package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.AwardRecord;
import cn.muzisheng.lebo.model.Awards;
import cn.muzisheng.lebo.model.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AwardRecordService extends IService<AwardRecord> {
    boolean hasDrawnToday(String openId);
    void saveAwardRecord(String openId, String awardGoods);
    ResponseEntity<Result<Boolean>> updateAwards(List<Awards> awards);
    ResponseEntity<Result<Awards>> drawAward();
    ResponseEntity<Result<List<Awards>>> getAwardsList();
}
