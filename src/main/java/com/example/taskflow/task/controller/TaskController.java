package com.example.taskflow.task.controller;

import com.example.taskflow.common.result.ApiResult;
import com.example.taskflow.common.result.PageResult;
import com.example.taskflow.task.dto.TaskCreateRequest;
import com.example.taskflow.task.dto.TaskLogResponse;
import com.example.taskflow.task.dto.TaskQueryRequest;
import com.example.taskflow.task.dto.TaskResponse;
import com.example.taskflow.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.taskflow.task.dto.TaskUpdateRequest;
import java.util.List;
import com.example.taskflow.task.dto.TaskStatusUpdateRequest;

@Tag(name = "任务管理接口", description = "提供任务创建、查询、修改、删除、状态流转和日志查询功能")
@Validated
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "创建任务")
    @PostMapping
    public ApiResult<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ApiResult.success(response);
    }

    @Operation(summary = "分页查询任务")
    @GetMapping
    public ApiResult<PageResult<TaskResponse>> pageTasks(@Valid TaskQueryRequest request) {
        PageResult<TaskResponse> result = taskService.pageTasks(request);
        return ApiResult.success(result);
    }

    @Operation(summary = "查询任务详情")
    @GetMapping("/{id}")
    public ApiResult<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ApiResult.success(response);
    }

    @Operation(summary = "查询任务日志")
    @GetMapping("/{id}/logs")
    public ApiResult<List<TaskLogResponse>> listTaskLogs(@PathVariable Long id) {
        List<TaskLogResponse> logs = taskService.listTaskLogs(id);
        return ApiResult.success(logs);
    }

    @Operation(summary = "修改任务")
    @PutMapping("/{id}")
    public ApiResult<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        TaskResponse response = taskService.updateTask(id, request);
        return ApiResult.success(response);
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteTask(
            @PathVariable Long id,
            @RequestParam(required = false) String operatorName
    ) {
        taskService.deleteTask(id, operatorName);
        return ApiResult.success();
    }

    @Operation(summary = "修改任务状态")
    @PutMapping("/{id}/status")
    public ApiResult<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        TaskResponse response = taskService.updateTaskStatus(id, request);
        return ApiResult.success(response);
    }
}