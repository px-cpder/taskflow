package com.example.taskflow.auth.service;

import com.example.taskflow.auth.dto.CurrentUserResponse;
import com.example.taskflow.auth.dto.LoginRequest;
import com.example.taskflow.auth.dto.LoginResponse;
import com.example.taskflow.auth.dto.RegisterRequest;

public interface AuthService {

    /**
     * 用户注册
     *
     * @param request 注册请求参数
     * @return 当前注册用户信息
     */
    CurrentUserResponse register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求参数
     * @return 登录结果，包含 token 和用户信息
     */
    LoginResponse login(LoginRequest request);

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户信息
     */
    CurrentUserResponse getCurrentUser();
}