package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.ConversionDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.UserPointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 用户积分接口接口
 * 提供用户积分的查询、更新等功能
 */
@RestController
@RequestMapping("/point")
public class UserPointApi {
    private final UserPointService userPointService;
    public UserPointApi(UserPointService userPointService) {
        this.userPointService = userPointService;
    }
    /**
     * 用户积分扣减，用于兑换积分商品
     * @param conversionDTO 积分兑换DTO,包含商品ID、数量等信息
     * @return 是否兑换成功
     */
    @PostMapping("/convert")
    public ResponseEntity<Result<Boolean>> convert(@RequestBody ConversionDTO conversionDTO) {
        return userPointService.convert(conversionDTO);
    }
   }
