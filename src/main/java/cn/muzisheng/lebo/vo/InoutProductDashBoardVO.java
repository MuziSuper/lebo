package cn.muzisheng.lebo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InoutProductDashBoardVO {
    /**
     * 昨日盈利金额（昨日所有已完成订单的总金额）
     */
    private Long yesterdayProfit;
    /**
     * 当前用户人数
     */
    private Long currentUserCount;
    /**
     * 当前库存总数量
     */
    private Long currentlyTotalStock;
    /**
     * 当前库存总金额
     */
    private Long currentLyTotalAmount;
}
