package com.example.taskflow.task.dto;

import com.example.taskflow.common.enums.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUpdateRequest {

    @Size(max = 100, message = "任务标题不能超过100个字符")
    private String title;

    @Size(max = 2000, message = "任务描述不能超过2000个字符")
    private String description;

    @Size(max = 50, message = "负责人名称不能超过50个字符")
    private String assigneeName;

    private TaskPriority priority;

    @Future(message = "截止时间必须是未来时间")
    private LocalDateTime deadline;

    @Size(max = 50, message = "操作人名称不能超过50个字符")
    private String operatorName;

    @Size(max = 255, message = "备注不能超过255个字符")
    private String remark;
}