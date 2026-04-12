package cn.muzisheng.lebo.interceptor;

import cn.muzisheng.lebo.exception.AuthorizationException;
import cn.muzisheng.lebo.utils.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Log4j2
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        log.info("【WebSocket握手开始】URI: {}", request.getURI());

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.warn("【握手失败】非 Servlet 请求，拒绝 WebSocket 连接");
            return false;
        }

        String token = servletRequest.getServletRequest().getParameter("token");
        String clientType = servletRequest.getServletRequest().getParameter("type");

        log.info("【握手参数】clientType: {}, token存在: {}", clientType, token != null);

        String openId = jwtUtil.getOpenidFromToken(token);

        if (openId == null || openId.trim().isEmpty()) {
            log.warn("【握手失败】token 解析后 openId 为空");
            throw new AuthorizationException("token 解析后为空");
        }

        attributes.put("openId", openId);
        log.info("【握手成功】openId: {}, clientType: {}", openId, clientType);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            log.error("【握手异常】URI: {}, error: {}", request.getURI(), exception.getMessage());
        } else {
            log.info("【握手完成】URI: {}", request.getURI());
        }
    }
}