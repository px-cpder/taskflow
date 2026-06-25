package com.example.taskflow.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    /**
     * 用户ID
     *
     * 使用 MyBatis-Plus 雪花算法生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     *
     * 用于登录，要求唯一。
     */
    private String username;

    /**
     * 加密后的密码
     *
     * 使用 BCryptPasswordEncoder 加密后保存。
     */
    private String password;

    /**
     * 用户昵称
     *
     * 业务中可以作为任务创建人、操作人名称。
     */
    private String nickname;

    /**
     * 用户角色
     *
     * USER：普通用户
     * ADMIN：管理员
     */
    private String role;

    /**
     * 用户状态
     *
     * 1：正常
     * 0：禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除字段
     *
     * 0：未删除
     * 1：已删除
     */
    @TableLogic
    private Integer deleted;
}