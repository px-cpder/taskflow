package com.example.taskflow.task.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    private String priority;

    private String creatorName;

    private String assigneeName;

    private LocalDateTime deadline;

    private LocalDateTime completedAt;

    private String aiSummary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}