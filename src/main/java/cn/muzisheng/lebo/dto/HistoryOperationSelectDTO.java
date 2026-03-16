package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 历史操作记录查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryOperationSelectDTO {
    /**
     * 操作类型，0: 商品添加，1: 商品修改，2: 商品删除，3: 手动备份数据，4: 自动备份数据，
     * 5: 后台系统登录，6: 商品分类添加，7: 商品分类修改，8: 商品分类删除
     */
    private Integer type;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 操作开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String startTime;
    /**
     * 操作结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;
    /**
     * 操作内容（模糊查询）
     */
    private String content;
    /**
     * 用户名称（模糊查询）
     */
    private String userName;
}
