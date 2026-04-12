package cn.muzisheng.lebo.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PointConversionParam {
        private final Map<String, Long> newStorageMap;
        private final long totalRequiredPoints;
        
        
    }