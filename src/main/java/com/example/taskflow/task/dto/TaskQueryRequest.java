package com.example.taskflow.task.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TaskQueryRequest {

    /**
     * 当前页
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数必须大于等于1")
    private Integer pageSize = 10;

    /**
     * 关键词：按标题、描述模糊查询
     */
    private String keyword;

    /**
     * 任务状态：
     * TODO / IN_PROGRESS / DONE / CANCELLED / OVERDUE
     */
    private String status;

    /**
     * 优先级：
     * LOW / MEDIUM / HIGH / URGENT
     */
    private String priority;

    /**
     * 负责人名称
     */
    private String assigneeName;
}