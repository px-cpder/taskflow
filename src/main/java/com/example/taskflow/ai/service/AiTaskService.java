package com.example.taskflow.ai.service;

import com.example.taskflow.ai.dto.AiProcessRecordResponse;

public interface AiTaskService {

    /**
     * AI 拆解任务
     *
     * @param taskId 任务ID
     * @return AI 处理记录
     */
    AiProcessRecordResponse splitTask(Long taskId);

    /**
     * AI 总结任务
     *
     * @param taskId 任务ID
     * @return AI 处理记录
     */
    AiProcessRecordResponse summaryTask(Long taskId);

    /**
     * AI 分析任务风险
     *
     * @param taskId 任务ID
     * @return AI 处理记录
     */
    AiProcessRecordResponse riskTask(Long taskId);

    /**
     * 根据记录ID查询 AI 处理记录
     *
     * @param recordId AI处理记录ID
     * @return AI 处理记录
     */
    AiProcessRecordResponse getRecordById(Long recordId);
}