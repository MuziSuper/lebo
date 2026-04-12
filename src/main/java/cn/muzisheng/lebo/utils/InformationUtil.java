package cn.muzisheng.lebo.utils;

import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.entity.Information;
import cn.muzisheng.lebo.entity.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InformationUtil {

    private InformationUtil() {
    }

    public static Information buildPointConversionNotification(
            String openId,
            List<ProductInOutDTO> productInOutDTOList,
            Map<String, Product> productMap,
            long totalRequiredPoints,
            long afterAmount) {
        String content = buildPointConversionContent(productInOutDTOList, productMap, totalRequiredPoints, afterAmount);
        
        return Information.builder()
                .id(IdUtil.generateId())
                .informationId(IdUtil.generateInformationId())
                .openId(openId)
                .subject("积分兑换成功通知")
                .content(content)
                .type(2)
                .deleted(0)
                .build();
    }

    private static String buildPointConversionContent(
            List<ProductInOutDTO> productInOutDTOList,
            Map<String, Product> productMap,
            long totalRequiredPoints,
            long afterAmount) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("您已成功兑换以下商品：\n");
        
        for (ProductInOutDTO dto : productInOutDTOList) {
            Product product = productMap.get(dto.getProductId());
            long number = Math.abs(Optional.ofNullable(dto.getNumber()).orElse(0L));
            Long creditsExchange = Optional.ofNullable(product.getCreditsExchange()).orElse(0L);
            long itemPoints = creditsExchange * number;
            
            contentBuilder.append("- ").append(product.getName())
                    .append(" x").append(number)
                    .append("（消耗").append(itemPoints)
                    .append("积分）\n");
        }
        
        contentBuilder.append("共消耗积分：").append(totalRequiredPoints).append("\n");
        contentBuilder.append("剩余积分：").append(afterAmount);
        
        return contentBuilder.toString();
    }

    public static Information buildCustomNotification(
            String openId,
            String subject,
            String content,
            Integer type) {
        return Information.builder()
                .id(IdUtil.generateId())
                .informationId(IdUtil.generateInformationId())
                .openId(openId)
                .subject(subject)
                .content(content)
                .type(type)
                .deleted(0)
                .build();
    }

    public static Information buildSystemNotification(
            String openId,
            String subject,
            String content) {
        return buildCustomNotification(openId, subject, content, 1);
    }

    public static Information buildActivityNotification(
            String openId,
            String subject,
            String content) {
        return buildCustomNotification(openId, subject, content, 0);
    }

    public static Information buildPersonalNotification(
            String openId,
            String subject,
            String content) {
        return buildCustomNotification(openId, subject, content, 2);
    }
}
