package com.example.taskflow.auth.security;

import com.example.taskflow.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    /**
     * JWT 配置属性
     *
     * 包含密钥和过期时间。
     */
    private final JwtProperties jwtProperties;

    /**
     * 生成 JWT 签名 Key
     *
     * @return 签名 Key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token
     *
     * @param loginUser 当前登录用户
     * @return JWT token 字符串
     */
    public String generateToken(LoginUser loginUser) {
        // 当前时间
        Date now = new Date();

        // 过期时间
        Date expireAt = new Date(now.getTime() + jwtProperties.getExpireHours() * 60 * 60 * 1000);

        return Jwts.builder()
                .setSubject(String.valueOf(loginUser.getUserId()))
                .claim("username", loginUser.getUsername())
                .claim("nickname", loginUser.getNickname())
                .claim("role", loginUser.getRole())
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 JWT token
     *
     * @param token JWT token
     * @return Claims 载荷信息
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 token 中解析当前登录用户
     *
     * @param token JWT token
     * @return 当前登录用户信息
     */
    public LoginUser parseLoginUser(String token) {
        Claims claims = parseToken(token);

        // 用户ID
        Long userId = Long.valueOf(claims.getSubject());

        // 用户名
        String username = claims.get("username", String.class);

        // 昵称
        String nickname = claims.get("nickname", String.class);

        // 角色
        String role = claims.get("role", String.class);

        return new LoginUser(userId, username, nickname, role);
    }

    /**
     * 获取 token 过期秒数
     *
     * @return token 有效期，单位秒
     */
    public Long getExpiresInSeconds() {
        return jwtProperties.getExpireHours() * 60 * 60;
    }
}