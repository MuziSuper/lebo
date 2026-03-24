package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.PointRecordListDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.PointRecordService;
import cn.muzisheng.lebo.vo.PointRecordListVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户积分记录接口
 * 提供用户积分记录的查询功能
 */
@Log4j2
@RestController
@RequestMapping("/pointRecord")
public class UserPointRecordApi {

    private final PointRecordService pointRecordService;

    public UserPointRecordApi(PointRecordService pointRecordService) {
        this.pointRecordService = pointRecordService;
    }

    /**
     * 获取用户积分记录列表
     * 支持按用户openId、创建时间范围筛选
     *
     * @param pointRecordListDTO 查询条件
     * @return 分页积分记录列表
     */
    @PostMapping("/list")
    public ResponseEntity<Result<IPage<PointRecordListVO>>> list(@RequestBody PointRecordListDTO pointRecordListDTO) {
        return pointRecordService.getPointRecordByOrderId(pointRecordListDTO);
    }
}
