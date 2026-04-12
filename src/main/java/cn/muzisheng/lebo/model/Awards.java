package cn.muzisheng.lebo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Awards {
    private BigDecimal probability;
    private String goods;
    private Boolean isPoint;
}
