package com.example.taskflow.sse.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SseMessage<T> {

    /**
     * 事件类型
     *
     * 例如：
     * task.created
     * task.updated
     * task.deleted
     * task.status.changed
     * task.overdue
     */
    private String eventType;

    /**
     * 消息说明
     *
     * 用于前端展示简单提示文本。
     */
    private String message;

    /**
     * 事件数据
     *
     * 可以是任务详情、任务ID、统计信息等。
     */
    private T data;

    /**
     * 消息创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建 SSE 消息对象
     *
     * @param eventType 事件类型
     * @param message   消息说明
     * @param data      事件数据
     * @param <T>       数据类型
     * @return SSE 消息对象
     */
    public static <T> SseMessage<T> of(String eventType, String message, T data) {
        SseMessage<T> sseMessage = new SseMessage<>();

        // 设置事件类型
        sseMessage.setEventType(eventType);

        // 设置前端展示消息
        sseMessage.setMessage(message);

        // 设置具体业务数据
        sseMessage.setData(data);

        // 设置消息创建时间
        sseMessage.setCreatedAt(LocalDateTime.now());

        return sseMessage;
    }
}