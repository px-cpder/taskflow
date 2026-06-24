package com.example.taskflow.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_process_record")
public class AiProcessRecord {

    /**
     * AI处理记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * AI处理类型：
     * SPLIT / SUMMARY / RISK / PRIORITY
     */
    private String type;

    /**
     * 处理状态：
     * PROCESSING / COMPLETED / FAILED
     */
    private String status;

    /**
     * 发送给大模型的提示词
     */
    private String prompt;

    /**
     * 大模型返回结果
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMessage;

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
}