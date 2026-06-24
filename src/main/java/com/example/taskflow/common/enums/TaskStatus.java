package com.example.taskflow.common.enums;

public enum TaskStatus {

    TODO("待处理"),
    IN_PROGRESS("进行中"),
    DONE("已完成"),
    CANCELLED("已取消"),
    OVERDUE("已逾期");

    private final String desc;

    TaskStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 判断是否是最终状态
     */
    public boolean isFinalStatus() {
        return this == DONE || this == CANCELLED;
    }
}