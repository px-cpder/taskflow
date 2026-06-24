package com.example.taskflow.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_log")
public class TaskLog {

    /**
     * 日志ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 旧状态
     */
    private String oldStatus;

    /**
     * 新状态
     */
    private String newStatus;

    /**
     * 操作类型：
     * CREATE / UPDATE / STATUS_CHANGE / DELETE / OVERDUE / AI_SUMMARY
     */
    private String operationType;

    /**
     * 操作备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}