package cn.muzisheng.lebo.interceptor;

import cn.muzisheng.lebo.exception.AuthorizationException;
import cn.muzisheng.lebo.utils.JwtUtil;
import cn.muzisheng.lebo.utils.UserThreadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT鉴权拦截器
 * 用于在请求处理前验证并解析JWT token
 */
@Log4j2
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 打印请求信息，方便排查
        log.info("拦截请求：{} {}, headers: {}", method, requestURI, request.getHeader("Authorization"));

        // 1. 获取Authorization请求头
        String authHeader = request.getHeader("Authorization");

        // 2. 检查token是否存在
        if (authHeader == null || authHeader.trim().isEmpty()) {
            log.warn("token 不存在");
            throw new AuthorizationException("token 不存在");
        }

        // 3. 验证token格式（应该以Bearer 开头）
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("token 格式不正确");
            throw new AuthorizationException("token 格式不正确");
        }

        // 4. 提取token
        String token = authHeader.substring(7);

        // 5. 解析token获取openid
        String openid = jwtUtil.getOpenidFromToken(token);

        // 6. 如果openid为空，说明token格式不正确
        if (openid == null || openid.trim().isEmpty()) {
            log.warn("token 解析后为空");
            throw new AuthorizationException("token 解析后为空");
        }
        // 7. 将openid设置到请求级别上下文
        UserThreadUtil.setCurrentOpenId(openid);

        // 8. 放行请求
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理请求级别上下文
        UserThreadUtil.removeCurrentOpenId();
    }

}
