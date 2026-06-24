package com.example.taskflow.task.dto;

import com.example.taskflow.common.enums.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题不能超过100个字符")
    private String title;

    @Size(max = 2000, message = "任务描述不能超过2000个字符")
    private String description;

    /**
     * 创建人名称。
     * 不传时后端默认设置为“系统用户”。
     */
    @Size(max = 50, message = "创建人名称不能超过50个字符")
    private String creatorName;

    /**
     * 负责人名称。
     */
    @Size(max = 50, message = "负责人名称不能超过50个字符")
    private String assigneeName;

    /**
     * LOW / MEDIUM / HIGH / URGENT
     */
    private TaskPriority priority;

    /**
     * 截止时间。
     */
    @Future(message = "截止时间必须是未来时间")
    private LocalDateTime deadline;
}