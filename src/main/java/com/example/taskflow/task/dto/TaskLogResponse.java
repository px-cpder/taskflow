package com.example.taskflow.task.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskLogResponse {

    private Long id;

    private Long taskId;

    private String operatorName;

    private String oldStatus;

    private String newStatus;

    private String operationType;

    private String remark;

    private LocalDateTime createdAt;
}