package com.example.taskflow.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {

    /**
     * 任务ID
     * 使用 MyBatis-Plus 雪花算法生成ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务状态：
     * TODO / IN_PROGRESS / DONE / CANCELLED / OVERDUE
     */
    private String status;

    /**
     * 任务优先级：
     * LOW / MEDIUM / HIGH / URGENT
     */
    private String priority;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 负责人名称
     */
    private String assigneeName;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * AI生成的任务总结
     */
    private String aiSummary;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

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
     * 逻辑删除：
     * 0 未删除
     * 1 已删除
     */
    @TableLogic
    private Integer deleted;
}