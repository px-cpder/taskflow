package com.example.taskflow.auth.dto;

import lombok.Data;

@Data
public class LoginResponse {

    /**
     * JWT token
     *
     * 前端后续请求接口时，需要放到请求头：
     * Authorization: Bearer token
     */
    private String token;

    /**
     * token 类型
     */
    private String tokenType = "Bearer";

    /**
     * token 过期时间，单位秒
     */
    private Long expiresIn;

    /**
     * 当前登录用户信息
     */
    private CurrentUserResponse user;
}