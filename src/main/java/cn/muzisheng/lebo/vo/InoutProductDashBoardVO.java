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
     * 昨日入库数量
     */
    private Long yesterdayTotalInNumber;
    /**
     * 昨日出库数量
     */
    private Long yesterdayTotalOutNumber;
    /**
     * 当前库存总数量
     */
    private Long currentlyTotalStock;
    /**
     * 当前库存总金额
     */
    private Long currentLyTotalAmount;
}
