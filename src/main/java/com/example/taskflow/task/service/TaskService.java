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
    /**
     * 修改任务状态
     *
     * @param id      任务ID
     * @param request 状态修改请求参数
     * @return 修改后的任务信息
     */
    TaskResponse updateTask(Long id, TaskUpdateRequest request);

    /**
     * 删除任务
     *
     * @param id           任务ID
     * @param operatorName 操作人名称
     */
    void deleteTask(Long id, String operatorName);

    TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request);
    /**
     * 扫描并处理逾期任务
     *
     * @param batchSize 每次最多处理的任务数量
     * @return 本次成功标记为逾期的任务数量
     */
    int scanAndMarkOverdueTasks(Integer batchSize);
}