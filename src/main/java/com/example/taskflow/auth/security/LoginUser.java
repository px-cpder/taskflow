package com.example.taskflow.auth.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 当前登录用户名
     */
    private String username;

    /**
     * 当前登录用户昵称
     */
    private String nickname;

    /**
     * 当前登录用户角色
     */
    private String role;
}