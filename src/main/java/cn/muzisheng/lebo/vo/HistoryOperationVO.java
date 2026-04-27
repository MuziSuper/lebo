package cn.muzisheng.lebo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryOperationVO {
    private String id;
    private String content;
    private Integer type;
    private String operatorId;
    private String operatorName;
    private LocalDateTime time;
}
