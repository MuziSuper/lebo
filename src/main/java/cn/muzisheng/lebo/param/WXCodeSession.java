package cn.muzisheng.lebo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WXCodeSession {
    private String openId;
    private String sessionKey;
    private String unionId;
    public static WXCodeSession of(Map<String, Object> map) throws Exception{
        WXCodeSession wxCodeSession = new WXCodeSession();
        wxCodeSession.setOpenId((String) map.get("openid"));
        wxCodeSession.setSessionKey((String) map.get("session_key"));
        wxCodeSession.setUnionId((String) map.get("unionid"));
        return wxCodeSession;
    }
}
