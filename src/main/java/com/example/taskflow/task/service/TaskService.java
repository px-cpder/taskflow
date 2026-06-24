package com.example.taskflow.task.service;

import com.example.taskflow.common.result.PageResult;
import com.example.taskflow.task.dto.*;

import java.util.List;

public interface TaskService {

    /**
     * 创建任务
     */
    TaskResponse createTask(TaskCreateRequest request);

    /**
     * 分页查询任务
     */
    PageResult<TaskResponse> pageTasks(TaskQueryRequest request);

    /**
     * 查询任务详情
     */
    TaskResponse getTaskById(Long id);

    /**
     * 查询任务日志
     */
    List<TaskLogResponse> listTaskLogs(Long taskId);

    TaskResponse updateTask(Long id, TaskUpdateRequest request);

    void deleteTask(Long id, String operatorName);
    TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request);
}