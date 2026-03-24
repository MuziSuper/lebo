package cn.muzisheng.lebo.utils;



import cn.muzisheng.lebo.config.TokenConfig;
import cn.muzisheng.lebo.constant.Constant;
import cn.muzisheng.lebo.exception.AuthorizationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Log4j2
@Component
public class JwtUtil {

    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    /**
     * 过期时间(单位:秒)
     */
    private final long JWT_EXPIRE;
    /**
     * JWT令牌头部前缀
     */
    private final String JWT_HEAD;
    /**
     * JWT签发者
     */
    private final String JWT_ISSUE;
    /**
     * JWT主题
     */
    private final String JWT_SUBJECT;
    /**
     * 加密密钥
     */
    private final SecretKey KEY;

    /**
     * 构造函数，通过TokenConfig配置初始化JWT工具类
     */
    public JwtUtil(TokenConfig tokenConfig) {
        // 从配置中获取各项参数
        this.JWT_EXPIRE = tokenConfig.getExpire();
        this.JWT_HEAD = tokenConfig.getHead();
        this.JWT_ISSUE = tokenConfig.getIssue();
        this.JWT_SUBJECT = tokenConfig.getSubject();

        try {
            // 解码Base64格式的盐值，用于生成密钥
            byte[] decode = Base64.getDecoder().decode(Constant.TOKEN_DEFAULT_SALT);
            // 创建HMAC-SHA256算法的密钥
            this.KEY = new SecretKeySpec(decode, 0, decode.length, "HmacSHA256");
        } catch (IllegalArgumentException | WeakKeyException e) {
            // 盐值太弱时抛出异常
            throw new JwtException("The salt is too weak.");
        }
    }

    /**
     * 根据openid生成token
     */
    public String generateToken(String openid) {
        // 生成UUID作为JWT ID
        String uuid = IdUtil.generateId();
        // 计算过期时间
        Date expireDate = Date.from(Instant.now().plusSeconds(JWT_EXPIRE));
        // 构建JWT
        String jwtStr = Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HmacSHA256")
                .and()
                .claim("openid", openid)
                .id(uuid)
                .expiration(expireDate)
                .issuedAt(new Date())
                .subject(JWT_SUBJECT)
                .issuer(JWT_ISSUE)
                .signWith(KEY, ALGORITHM)
                .compact();
        // 返回带前缀的完整令牌
        return JWT_HEAD + jwtStr;
    }

    /**
     * 刷新令牌（基于openid重新生成）
     *
     * @param token 原令牌
     * @return 新生成的令牌
     */
    public String refreshToken(String token) {
        // 从原令牌中提取openid
        String openid = getOpenidFromToken(token);
        // 使用相同的openid生成新令牌
        return generateToken(openid);
    }

    /**
     * 从token中获取openid,如果返回null,说明token已经过期
     *
     * @throws AuthorizationException bad token or token expired
     */
    public String getOpenidFromToken(String token) {
        // 解析令牌获取声明
        Jws<Claims> claimsJws = getClaimFromToken(token);
        Claims claims = claimsJws.getPayload();
        // 从声明中提取openid
        return claims.get("openid", String.class);
    }


    /**
     * 删除令牌前缀
     *
     * @param token 带前缀的完整令牌
     * @return 纯JWT令牌（不含前缀）
     */
    public String delTokenPrefix(String token) {
        return token.replace(Constant.TOKEN_DEFAULT_SECRET_PREFIX, "");
    }

    /**
     * 从token中获取Claim
     *
     * @throws AuthorizationException bad token or token expired
     */
    private Jws<Claims> getClaimFromToken(String token) {
        Jws<Claims> claims;
        try {
            // 解析并验证令牌签名
            claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(delTokenPrefix(token));
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // 令牌格式错误或不支持时抛出异常
            throw new AuthorizationException("bad token", e);
        } catch (ExpiredJwtException e) {
            // 令牌已过期时抛出异常
            throw new AuthorizationException("token expired", e);
        }
        return claims;
    }
}