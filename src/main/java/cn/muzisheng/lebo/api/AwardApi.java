package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.model.Awards;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.AwardRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/awards")
public class AwardApi {

    private final AwardRecordService awardRecordService;

    public AwardApi(AwardRecordService awardRecordService) {
        this.awardRecordService = awardRecordService;
    }

    @PostMapping("/update")
    public ResponseEntity<Result<Boolean>> updateAwards(@RequestBody List<Awards> awards) {
        return awardRecordService.updateAwards(awards);
    }

    @GetMapping("/list")
    public ResponseEntity<Result<List<Awards>>> getAwardsList() {
        return awardRecordService.getAwardsList();
    }

    @GetMapping("/draw")
    public ResponseEntity<Result<Awards>> drawAward() {
        return awardRecordService.drawAward();
    }
}
