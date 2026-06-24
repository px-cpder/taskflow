package com.example.taskflow.ai.controller;

import com.example.taskflow.ai.dto.AiProcessRecordResponse;
import com.example.taskflow.ai.service.AiTaskService;
import com.example.taskflow.common.result.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AiTaskController {

    /**
     * AI 任务处理服务
     *
     * 用于执行任务拆解、任务总结、风险分析以及查询 AI 处理记录。
     */
    private final AiTaskService aiTaskService;

    /**
     * AI 拆解任务
     *
     * @param taskId 任务ID
     * @return AI处理记录
     */
    @Operation(summary = "AI拆解任务")
    @PostMapping("/api/tasks/{taskId}/ai/split")
    public ApiResult<AiProcessRecordResponse> splitTask(@PathVariable Long taskId) {
        AiProcessRecordResponse response = aiTaskService.splitTask(taskId);
        return ApiResult.success(response);
    }

    /**
     * AI 总结任务
     *
     * @param taskId 任务ID
     * @return AI处理记录
     */
    @Operation(summary = "AI总结任务")
    @PostMapping("/api/tasks/{taskId}/ai/summary")
    public ApiResult<AiProcessRecordResponse> summaryTask(@PathVariable Long taskId) {
        AiProcessRecordResponse response = aiTaskService.summaryTask(taskId);
        return ApiResult.success(response);
    }

    /**
     * AI 分析任务风险
     *
     * @param taskId 任务ID
     * @return AI处理记录
     */
    @Operation(summary = "AI分析任务风险")
    @PostMapping("/api/tasks/{taskId}/ai/risk")
    public ApiResult<AiProcessRecordResponse> riskTask(@PathVariable Long taskId) {
        AiProcessRecordResponse response = aiTaskService.riskTask(taskId);
        return ApiResult.success(response);
    }

    /**
     * 查询 AI 处理记录
     *
     * @param recordId AI处理记录ID
     * @return AI处理记录
     */
    @Operation(summary = "查询AI处理记录")
    @GetMapping("/api/ai/records/{recordId}")
    public ApiResult<AiProcessRecordResponse> getRecordById(@PathVariable Long recordId) {
        AiProcessRecordResponse response = aiTaskService.getRecordById(recordId);
        return ApiResult.success(response);
    }
}