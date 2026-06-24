package com.example.taskflow.common.enums;

public enum TaskPriority {

    LOW("低"),
    MEDIUM("中"),
    HIGH("高"),
    URGENT("紧急");

    private final String desc;

    TaskPriority(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}