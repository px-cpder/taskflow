package com.example.taskflow.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.taskflow.auth.dto.CurrentUserResponse;
import com.example.taskflow.auth.dto.LoginRequest;
import com.example.taskflow.auth.dto.LoginResponse;
import com.example.taskflow.auth.dto.RegisterRequest;
import com.example.taskflow.auth.security.JwtUtils;
import com.example.taskflow.auth.security.LoginUser;
import com.example.taskflow.auth.security.SecurityUtils;
import com.example.taskflow.auth.service.AuthService;
import com.example.taskflow.common.exception.BusinessException;
import com.example.taskflow.user.entity.SysUser;
import com.example.taskflow.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * 用户表 Mapper
     *
     * 用于注册、登录时查询和保存用户信息。
     */
    private final SysUserMapper sysUserMapper;

    /**
     * 密码加密器
     *
     * 用于注册时加密密码，登录时校验密码。
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * JWT 工具类
     *
     * 用于登录成功后生成 token。
     */
    private final JwtUtils jwtUtils;

    /**
     * 用户注册
     *
     * @param request 注册请求参数
     * @return 注册后的用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurrentUserResponse register(RegisterRequest request) {
        // 判断用户名是否已经存在
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())
        );

        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户实体
        SysUser user = new SysUser();

        // 设置用户名
        user.setUsername(request.getUsername());

        // 密码加密后保存
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 设置昵称
        user.setNickname(request.getNickname());

        // 默认普通用户
        user.setRole("USER");

        // 默认状态正常
        user.setStatus(1);

        // 默认未删除
        user.setDeleted(0);

        int inserted = sysUserMapper.insert(user);

        if (inserted <= 0) {
            throw new BusinessException("用户注册失败");
        }

        return convertToCurrentUserResponse(user);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求参数
     * @return 登录响应，包含 token
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())
                        .last("LIMIT 1")
        );

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 校验明文密码和加密密码是否匹配
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new BusinessException("用户名或密码错误");
        }

        // 构造登录用户对象
        LoginUser loginUser = new LoginUser(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole()
        );

        // 生成 JWT token
        String token = jwtUtils.generateToken(loginUser);

        // 构造登录响应
        LoginResponse response = new LoginResponse();

        // 设置 token
        response.setToken(token);

        // 设置 token 类型
        response.setTokenType("Bearer");

        // 设置过期时间
        response.setExpiresIn(jwtUtils.getExpiresInSeconds());

        // 设置当前用户信息
        response.setUser(convertToCurrentUserResponse(user));

        return response;
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户信息
     */
    @Override
    public CurrentUserResponse getCurrentUser() {
        // 从 SecurityContext 中获取当前登录用户
        LoginUser loginUser = SecurityUtils.getCurrentUser();

        if (loginUser == null) {
            throw new BusinessException("请先登录");
        }

        CurrentUserResponse response = new CurrentUserResponse();

        // 设置用户ID
        response.setId(loginUser.getUserId());

        // 设置用户名
        response.setUsername(loginUser.getUsername());

        // 设置昵称
        response.setNickname(loginUser.getNickname());

        // 设置角色
        response.setRole(loginUser.getRole());

        return response;
    }

    /**
     * 将用户实体转换为当前用户响应对象
     *
     * @param user 用户实体
     * @return 当前用户响应对象
     */
    private CurrentUserResponse convertToCurrentUserResponse(SysUser user) {
        if (user == null) {
            return null;
        }

        CurrentUserResponse response = new CurrentUserResponse();

        // 用户ID
        response.setId(user.getId());

        // 用户名
        response.setUsername(user.getUsername());

        // 昵称
        response.setNickname(user.getNickname());

        // 角色
        response.setRole(user.getRole());

        return response;
    }
}