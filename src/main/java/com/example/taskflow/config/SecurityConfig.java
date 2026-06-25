package com.example.taskflow.config;

import com.example.taskflow.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT 认证过滤器
     *
     * 用于从请求头中解析 token，并设置当前登录用户。
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器
     *
     * 注册时加密密码，登录时校验密码。
     *
     * @return BCrypt 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 过滤链配置
     *
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离项目通常关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 不使用 Session，改为 JWT 无状态认证
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 表单登录不用，关闭默认登录页
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP Basic 不用，关闭浏览器弹窗认证
                .httpBasic(AbstractHttpConfigurer::disable)

                // 跨域配置，先使用默认。如果你已有 WebConfig CORS，也可以保留
                .cors(Customizer.withDefaults())

                // 接口权限配置
                .authorizeHttpRequests(auth -> auth
                        // 登录注册放行
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // Knife4j / Swagger 文档放行
                        .requestMatchers(
                                "/doc.html",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // SSE 先放行，因为浏览器原生 EventSource 不方便加 Authorization 请求头
                        .requestMatchers("/api/sse/**").permitAll()

                        // OPTIONS 预检请求放行
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 其他接口都需要登录
                        .anyRequest().authenticated()
                )

                // 把 JWT 过滤器放到 UsernamePasswordAuthenticationFilter 前面
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}