package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.HistoryOperationSelectDTO;
import cn.muzisheng.lebo.entity.HistoryOperation;
import cn.muzisheng.lebo.model.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;

public interface HistoryOperationService {
    /**
     * 添加历史操作,
     * @param type 操作类型，0: 商品添加，1: 商品修改，2: 商品删除，3: 手动备份数据，4: 自动备份数据，5: 后台系统登录 6: 商品分类添加，7: 商品分类修改，8: 商品分类删除
     * @param content 操作内容
     */
    void addHistoryOperation(Integer type, String content);

    /**
     * 获取历史操作记录列表（分页）
     * @param dto 查询条件DTO
     * @return 分页历史操作记录
     */
    ResponseEntity<Result<IPage<HistoryOperation>>> getHistoryOperation(HistoryOperationSelectDTO dto);
}
