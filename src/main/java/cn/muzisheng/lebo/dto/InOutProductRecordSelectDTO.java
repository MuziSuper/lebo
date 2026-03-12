package cn.muzisheng.lebo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InOutProductRecordSelectDTO {
    /**
     * 商品ID
     */
    private String id;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 出入库，1: 入库, 2: 出库
     */
    private Integer type;
    /**
     * 出入库时间区间，开始时间
     */
    private LocalDateTime startTime;
    /**
     * 出入库时间区间，结束时间
     */
    private LocalDateTime endTime;

}
