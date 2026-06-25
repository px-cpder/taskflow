package com.example.taskflow.auth.controller;

import com.example.taskflow.auth.dto.CurrentUserResponse;
import com.example.taskflow.auth.dto.LoginRequest;
import com.example.taskflow.auth.dto.LoginResponse;
import com.example.taskflow.auth.dto.RegisterRequest;
import com.example.taskflow.auth.service.AuthService;
import com.example.taskflow.common.result.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口", description = "提供用户注册、登录和获取当前用户信息功能")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 认证服务
     *
     * 用于处理注册、登录、当前用户查询等认证相关业务。
     */
    private final AuthService authService;

    /**
     * 用户注册
     *
     * @param request 注册请求参数
     * @return 注册后的用户信息
     */
    @Operation(summary = "用户注册", description = "注册新用户，密码会使用 BCrypt 加密后保存")
    @PostMapping("/register")
    public ApiResult<CurrentUserResponse> register(@Valid @RequestBody RegisterRequest request) {
        CurrentUserResponse response = authService.register(request);
        return ApiResult.success(response);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求参数
     * @return 登录结果，包含 JWT token
     */
    @Operation(summary = "用户登录", description = "用户名密码校验成功后返回 JWT token")
    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResult.success(response);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户
     */
    @Operation(summary = "获取当前用户", description = "根据 Authorization 请求头中的 token 获取当前登录用户信息")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ApiResult<CurrentUserResponse> me() {
        CurrentUserResponse response = authService.getCurrentUser();
        return ApiResult.success(response);
    }
}
