package com.example.taskflow.common.enums;

public enum AiProcessType {

    SPLIT("任务拆解"),
    SUMMARY("任务总结"),
    RISK("风险分析"),
    PRIORITY("优先级建议");

    private final String desc;

    AiProcessType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}