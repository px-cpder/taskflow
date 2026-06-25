package com.example.taskflow.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "taskflow.jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥
     *
     * 生产环境不要写死在配置文件中，应通过环境变量注入。
     */
    private String secret;

    /**
     * JWT 过期时间，单位小时
     */
    private Long expireHours = 24L;
}