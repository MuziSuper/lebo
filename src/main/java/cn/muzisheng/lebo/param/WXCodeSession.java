package cn.muzisheng.lebo.param;

import cn.muzisheng.lebo.exception.WXException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class WXCodeSession {
    private String openId;
    private String sessionKey;
    private String unionId;

    /**
     * 根据微信接口返回的Map数据创建WXCodeSession对象
     *
     * @param map 微信接口返回的数据映射，包含openid、session_key等字段
     * @return 封装好的WXCodeSession对象
     * @throws WXException 当数据解析失败时抛出异常
     */
    public static WXCodeSession of(Map<String, Object> map) {
        try {
            // 创建新的WXCodeSession实例
            WXCodeSession wxCodeSession = new WXCodeSession();
            // 从map中提取openid并设置到对象中
            wxCodeSession.setOpenId((String) map.get("openid"));
            // 从map中提取session_key并设置到对象中
            wxCodeSession.setSessionKey((String) map.get("session_key"));
            // 从map中提取unionid并设置到对象中（该字段可能为空）
            wxCodeSession.setUnionId((String) map.get("unionid"));
            // 返回完整构建的对象
            return wxCodeSession;
        } catch (Exception e) {
            // 记录异常日志
            log.error("封装微信codeSession失败", e);
            // 抛出业务异常，包含原始异常信息
            throw new WXException("封装微信codeSession失败", e);
        }
    }
}
