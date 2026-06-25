package com.example.taskflow.auth.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT 工具类
     *
     * 用于解析请求头中的 token。
     */
    private final JwtUtils jwtUtils;

    /**
     * 每次请求都会经过该过滤器
     *
     * 作用：
     * 1. 从 Authorization 请求头中读取 Bearer token
     * 2. 校验并解析 token
     * 3. 将当前登录用户放入 Spring Security 上下文
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 读取 Authorization 请求头
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 如果请求头为空或不是 Bearer token，直接放行，后续由 Spring Security 决定是否拦截
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 去掉 Bearer 前缀，得到真正的 token
        String token = authorization.substring(7);

        try {
            // 从 token 中解析登录用户
            LoginUser loginUser = jwtUtils.parseLoginUser(token);

            // 构造角色权限，Spring Security 要求角色通常以 ROLE_ 开头
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + loginUser.getRole())
            );

            // 构造认证对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);

            // 将认证对象放入 SecurityContext，后续业务代码可通过 SecurityUtils 获取当前用户
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            // token 解析失败时清空上下文，避免使用错误认证信息
            SecurityContextHolder.clearContext();
        }

        // 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
}