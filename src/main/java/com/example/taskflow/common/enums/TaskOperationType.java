package com.example.taskflow.common.enums;

public enum TaskOperationType {
    CREATE("创建任务"),
    UPDATE("修改任务"),
    STATUS_CHANGE("状态变更"),
    DELETE("删除任务"),
    OVERDUE("任务逾期"),
    AI_SUMMARY("AI总结");

    private final String desc;

    TaskOperationType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
