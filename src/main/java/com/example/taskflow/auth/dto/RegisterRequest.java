package com.example.taskflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    /**
     * 用户名
     *
     * 用于登录，必须唯一。
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    private String username;

    /**
     * 登录密码
     *
     * 后端会使用 BCrypt 加密保存，数据库不保存明文密码。
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度必须在6到30个字符之间")
    private String password;

    /**
     * 用户昵称
     *
     * 用于页面展示，也可以作为任务操作人名称。
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;
}