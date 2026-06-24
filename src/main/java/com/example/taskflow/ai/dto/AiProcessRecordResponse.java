package com.example.taskflow.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiProcessRecordResponse {

    /**
     * AI 处理记录ID
     */
    private Long id;

    /**
     * 关联的任务ID
     */
    private Long taskId;

    /**
     * AI 处理类型：
     * SPLIT / SUMMARY / RISK / PRIORITY
     */
    private String type;

    /**
     * AI 处理状态：
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
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}