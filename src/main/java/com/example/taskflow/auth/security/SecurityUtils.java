package com.example.taskflow.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 私有构造方法
     *
     * 工具类不需要实例化。
     */
    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户；未登录时返回 null
     */
    public static LoginUser getCurrentUser() {
        // 从 Spring Security 上下文中获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 获取认证主体
        Object principal = authentication.getPrincipal();

        if (principal instanceof LoginUser loginUser) {
            return loginUser;
        }

        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID；未登录时返回 null
     */
    public static Long getCurrentUserId() {
        LoginUser loginUser = getCurrentUser();
        return loginUser == null ? null : loginUser.getUserId();
    }

    /**
     * 获取当前登录用户昵称
     *
     * @return 用户昵称；未登录时返回“系统用户”
     */
    public static String getCurrentNicknameOrDefault() {
        LoginUser loginUser = getCurrentUser();
        return loginUser == null ? "系统用户" : loginUser.getNickname();
    }
}