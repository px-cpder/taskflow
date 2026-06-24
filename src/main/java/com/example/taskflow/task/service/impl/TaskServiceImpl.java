package com.example.taskflow.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.taskflow.common.enums.TaskOperationType;
import com.example.taskflow.common.enums.TaskPriority;
import com.example.taskflow.common.enums.TaskStatus;
import com.example.taskflow.common.exception.BusinessException;
import com.example.taskflow.common.result.PageResult;
import com.example.taskflow.task.dto.*;
import com.example.taskflow.task.entity.Task;
import com.example.taskflow.task.entity.TaskLog;
import com.example.taskflow.task.mapper.TaskLogMapper;
import com.example.taskflow.task.mapper.TaskMapper;
import com.example.taskflow.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 任务服务实现类
 * 负责任务的创建、查询、修改、删除以及状态流转等核心业务逻辑
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    private final TaskLogMapper taskLogMapper;

    /**
     * 创建新任务
     * 同时记录任务创建日志，设置默认状态为TODO，默认优先级为MEDIUM
     *
     * @param request 任务创建请求对象，包含标题、描述、优先级等信息
     * @return 创建后的任务响应对象
     * @throws BusinessException 当任务创建失败时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // 设置初始状态为待办
        task.setStatus(TaskStatus.TODO.name());

        // 设置优先级，默认为中等优先级
        TaskPriority priority = request.getPriority() == null
                ? TaskPriority.MEDIUM
                : request.getPriority();
        task.setPriority(priority.name());

        // 设置创建人名称，默认为"系统用户"
        String creatorName = StringUtils.hasText(request.getCreatorName())
                ? request.getCreatorName()
                : "系统用户";

        task.setCreatorName(creatorName);
        task.setAssigneeName(request.getAssigneeName());

        task.setDeadline(request.getDeadline());
        task.setCompletedAt(null);
        task.setAiSummary(null);
        task.setVersion(0);
        task.setDeleted(0);

        int inserted = taskMapper.insert(task);

        if (inserted <= 0) {
            throw new BusinessException("任务创建失败");
        }

        // 记录任务创建日志
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setOperatorName(creatorName);
        taskLog.setOldStatus(null);
        taskLog.setNewStatus(TaskStatus.TODO.name());
        taskLog.setOperationType(TaskOperationType.CREATE.name());
        taskLog.setRemark("创建任务");

        taskLogMapper.insert(taskLog);

        return convertToResponse(task);
    }

    /**
     * 分页查询任务列表
     * 支持按关键词、状态、优先级、负责人等条件筛选，按创建时间降序排列
     *
     * @param request 任务查询请求对象，包含分页参数和筛选条件
     * @return 分页结果，包含任务响应对象列表
     */
    @Override
    public PageResult<TaskResponse> pageTasks(TaskQueryRequest request) {
        // 设置默认分页参数，页码默认为1，每页大小默认为10，最大限制为100
        Integer pageNum = request.getPageNum() == null ? 1 : request.getPageNum();
        Integer pageSize = request.getPageSize() == null ? 10 : request.getPageSize();

        if (pageSize > 100) {
            pageSize = 100;
        }

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索：匹配标题或描述
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword();
            wrapper.and(w -> w
                    .like(Task::getTitle, keyword)
                    .or()
                    .like(Task::getDescription, keyword)
            );
        }

        // 按状态筛选并验证状态合法性
        if (StringUtils.hasText(request.getStatus())) {
            validateTaskStatus(request.getStatus());
            wrapper.eq(Task::getStatus, request.getStatus());
        }

        // 按优先级筛选并验证优先级合法性
        if (StringUtils.hasText(request.getPriority())) {
            validateTaskPriority(request.getPriority());
            wrapper.eq(Task::getPriority, request.getPriority());
        }

        // 按负责人名称模糊查询
        if (StringUtils.hasText(request.getAssigneeName())) {
            wrapper.like(Task::getAssigneeName, request.getAssigneeName());
        }

        // 按创建时间降序排列
        wrapper.orderByDesc(Task::getCreatedAt);

        Page<Task> page = new Page<>(pageNum, pageSize);
        Page<Task> taskPage = taskMapper.selectPage(page, wrapper);

        // 转换为响应对象
        List<TaskResponse> records = taskPage.getRecords()
                .stream()
                .map(this::convertToResponse)
                .toList();

        return PageResult.of(
                taskPage.getTotal(),
                taskPage.getPages(),
                taskPage.getCurrent(),
                taskPage.getSize(),
                records
        );
    }

    /**
     * 根据ID获取任务详情
     *
     * @param id 任务ID
     * @return 任务响应对象
     * @throws BusinessException 当任务ID为空或任务不存在时抛出异常
     */
    @Override
    public TaskResponse getTaskById(Long id) {
        if (id == null) {
            throw new BusinessException("任务ID不能为空");
        }

        Task task = taskMapper.selectById(id);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        return convertToResponse(task);
    }

    /**
     * 查询任务操作日志列表
     * 按创建时间降序排列，返回该任务的所有操作记录
     *
     * @param taskId 任务ID
     * @return 任务日志响应对象列表
     * @throws BusinessException 当任务ID为空或任务不存在时抛出异常
     */
    @Override
    public List<TaskLogResponse> listTaskLogs(Long taskId) {
        if (taskId == null) {
            throw new BusinessException("任务ID不能为空");
        }

        Task task = taskMapper.selectById(taskId);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        LambdaQueryWrapper<TaskLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskLog::getTaskId, taskId);
        wrapper.orderByDesc(TaskLog::getCreatedAt);

        List<TaskLog> logs = taskLogMapper.selectList(wrapper);

        return logs.stream()
                .map(this::convertToLogResponse)
                .toList();
    }

    /**
     * 验证任务状态的合法性
     *
     * @param status 任务状态字符串
     * @throws BusinessException 当状态不合法时抛出异常
     */
    private void validateTaskStatus(String status) {
        try {
            TaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("任务状态不合法：" + status);
        }
    }

    /**
     * 验证任务优先级的合法性
     *
     * @param priority 优先级字符串
     * @throws BusinessException 当优先级不合法时抛出异常
     */
    private void validateTaskPriority(String priority) {
        try {
            TaskPriority.valueOf(priority);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("任务优先级不合法：" + priority);
        }
    }

    /**
     * 将Task实体对象转换为TaskResponse响应对象
     *
     * @param task 任务实体对象
     * @return 任务响应对象
     */
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatorName(task.getCreatorName());
        response.setAssigneeName(task.getAssigneeName());
        response.setDeadline(task.getDeadline());
        response.setCompletedAt(task.getCompletedAt());
        response.setAiSummary(task.getAiSummary());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;
    }

    /**
     * 将TaskLog实体对象转换为TaskLogResponse响应对象
     *
     * @param taskLog 任务日志实体对象
     * @return 任务日志响应对象
     */
    private TaskLogResponse convertToLogResponse(TaskLog taskLog) {
        TaskLogResponse response = new TaskLogResponse();

        response.setId(taskLog.getId());
        response.setTaskId(taskLog.getTaskId());
        response.setOperatorName(taskLog.getOperatorName());
        response.setOldStatus(taskLog.getOldStatus());
        response.setNewStatus(taskLog.getNewStatus());
        response.setOperationType(taskLog.getOperationType());
        response.setRemark(taskLog.getRemark());
        response.setCreatedAt(taskLog.getCreatedAt());

        return response;
    }

    /**
     * 更新任务信息
     * 只更新请求中提供的非空字段，并记录修改日志
     *
     * @param id 任务ID
     * @param request 任务更新请求对象，包含要更新的字段和操作人信息
     * @return 更新后的任务响应对象
     * @throws BusinessException 当任务ID为空、任务不存在、没有需要修改的内容或更新失败时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        if (id == null) {
            throw new BusinessException("任务ID不能为空");
        }

        Task task = taskMapper.selectById(id);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        String operatorName = StringUtils.hasText(request.getOperatorName())
                ? request.getOperatorName()
                : "系统用户";

        boolean changed = false;

        // 逐个字段检查并更新，只要有任一字段被修改就标记为已变更
        if (StringUtils.hasText(request.getTitle())) {
            task.setTitle(request.getTitle());
            changed = true;
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
            changed = true;
        }

        if (request.getAssigneeName() != null) {
            task.setAssigneeName(request.getAssigneeName());
            changed = true;
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority().name());
            changed = true;
        }

        if (request.getDeadline() != null) {
            task.setDeadline(request.getDeadline());
            changed = true;
        }

        if (!changed) {
            throw new BusinessException("没有需要修改的任务内容");
        }

        int updated = taskMapper.updateById(task);

        if (updated <= 0) {
            throw new BusinessException("任务修改失败，可能存在并发修改，请刷新后重试");
        }

        // 记录任务修改日志，状态保持不变
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setOperatorName(operatorName);
        taskLog.setOldStatus(task.getStatus());
        taskLog.setNewStatus(task.getStatus());
        taskLog.setOperationType(TaskOperationType.UPDATE.name());
        taskLog.setRemark(StringUtils.hasText(request.getRemark()) ? request.getRemark() : "修改任务信息");

        taskLogMapper.insert(taskLog);

        Task latestTask = taskMapper.selectById(id);
        return convertToResponse(latestTask);
    }

    /**
     * 删除任务
     * 物理删除任务记录，并记录删除日志
     *
     * @param id 任务ID
     * @param operatorName 操作人名称，如果为空则默认为"系统用户"
     * @throws BusinessException 当任务ID为空、任务不存在或删除失败时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id, String operatorName) {
        if (id == null) {
            throw new BusinessException("任务ID不能为空");
        }

        Task task = taskMapper.selectById(id);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        String realOperatorName = StringUtils.hasText(operatorName)
                ? operatorName
                : "系统用户";

        int deleted = taskMapper.deleteById(id);

        if (deleted <= 0) {
            throw new BusinessException("任务删除失败");
        }

        // 记录任务删除日志
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setOperatorName(realOperatorName);
        taskLog.setOldStatus(task.getStatus());
        taskLog.setNewStatus(task.getStatus());
        taskLog.setOperationType(TaskOperationType.DELETE.name());
        taskLog.setRemark("删除任务");

        taskLogMapper.insert(taskLog);
    }

    /**
     * 更新任务状态
     * 验证状态流转的合法性，更新任务状态并完成时间，记录状态变更日志
     *
     * @param id 任务ID
     * @param request 状态更新请求对象，包含目标状态、操作人和备注信息
     * @return 更新后的任务响应对象
     * @throws BusinessException 当任务ID为空、任务不存在、状态重复、状态流转非法或更新失败时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request) {
        if (id == null) {
            throw new BusinessException("任务ID不能为空");
        }

        Task task = taskMapper.selectById(id);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        TaskStatus currentStatus = TaskStatus.valueOf(task.getStatus());
        TaskStatus targetStatus = request.getTargetStatus();

        // 检查是否与当前状态相同
        if (currentStatus == targetStatus) {
            throw new BusinessException("任务已经是该状态，无需重复修改");
        }

        // 验证状态流转是否合法
        if (!canTransfer(currentStatus, targetStatus)) {
            throw new BusinessException("非法状态流转：" + currentStatus.name() + " -> " + targetStatus.name());
        }

        String oldStatus = task.getStatus();

        task.setStatus(targetStatus.name());

        // 如果任务完成，设置完成时间；否则清空完成时间
        if (targetStatus == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }

        if (targetStatus != TaskStatus.DONE) {
            task.setCompletedAt(null);
        }

        int updated = taskMapper.updateById(task);

        if (updated <= 0) {
            throw new BusinessException("任务状态修改失败，可能存在并发修改，请刷新后重试");
        }

        String operatorName = StringUtils.hasText(request.getOperatorName())
                ? request.getOperatorName()
                : "系统用户";

        // 记录状态变更日志
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setOperatorName(operatorName);
        taskLog.setOldStatus(oldStatus);
        taskLog.setNewStatus(targetStatus.name());
        taskLog.setOperationType(TaskOperationType.STATUS_CHANGE.name());
        taskLog.setRemark(StringUtils.hasText(request.getRemark())
                ? request.getRemark()
                : "任务状态由 " + oldStatus + " 变更为 " + targetStatus.name());

        taskLogMapper.insert(taskLog);

        Task latestTask = taskMapper.selectById(id);
        return convertToResponse(latestTask);
    }

    /**
     * 判断任务状态流转是否合法
     * 定义状态流转规则：TODO->IN_PROGRESS/CANCELLED/OVERDUE，IN_PROGRESS->DONE/CANCELLED/OVERDUE，OVERDUE->DONE，DONE和CANCELLED不可再流转
     *
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return true表示状态流转合法，false表示不合法
     */
    private boolean canTransfer(TaskStatus currentStatus, TaskStatus targetStatus) {
        return switch (currentStatus) {
            case TODO -> Set.of(
                    TaskStatus.IN_PROGRESS,
                    TaskStatus.CANCELLED,
                    TaskStatus.OVERDUE
            ).contains(targetStatus);

            case IN_PROGRESS -> Set.of(
                    TaskStatus.DONE,
                    TaskStatus.CANCELLED,
                    TaskStatus.OVERDUE
            ).contains(targetStatus);

            case OVERDUE -> Set.of(
                    TaskStatus.DONE
            ).contains(targetStatus);

            case DONE, CANCELLED -> false;
        };
    }
}
