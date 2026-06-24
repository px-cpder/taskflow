package com.example.taskflow.task.dto;

import com.example.taskflow.common.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {

    /**
     * 目标状态：
     * TODO / IN_PROGRESS / DONE / CANCELLED / OVERDUE
     */
    @NotNull(message = "目标状态不能为空")
    private TaskStatus targetStatus;

    /**
     * 操作人名称
     */
    @Size(max = 50, message = "操作人名称不能超过50个字符")
    private String operatorName;

    /**
     * 状态变更备注
     */
    @Size(max = 255, message = "备注不能超过255个字符")
    private String remark;
}