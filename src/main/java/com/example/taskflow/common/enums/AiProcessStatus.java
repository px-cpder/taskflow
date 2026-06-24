package com.example.taskflow.common.enums;

public enum AiProcessStatus {
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    FAILED("处理失败");

    private final String desc;

    AiProcessStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
