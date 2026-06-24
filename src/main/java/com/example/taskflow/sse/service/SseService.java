package com.example.taskflow.sse.service;

import com.example.taskflow.sse.dto.SseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    /**
     * SSE 连接超时时间
     *
     * 0L 表示连接不过期。
     */
    private static final Long SSE_TIMEOUT = 0L;

    /**
     * Jackson JSON 序列化工具
     *
     * 用于将 SSE 消息对象转成 JSON 字符串，避免中文乱码和对象格式不稳定问题。
     */
    private final ObjectMapper objectMapper;

    /**
     * 保存所有 SSE 客户端连接
     *
     * key：客户端连接ID
     * value：SseEmitter 连接对象
     */
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 创建 SSE 连接
     *
     * @return SSE 长连接对象
     */
    public SseEmitter connect() {
        // 为当前客户端生成唯一连接ID
        String clientId = UUID.randomUUID().toString();

        // 创建 SSE 连接对象
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // 保存当前连接
        emitterMap.put(clientId, emitter);

        // 连接正常完成时移除连接
        emitter.onCompletion(() -> {
            emitterMap.remove(clientId);
            log.info("SSE 连接完成，clientId={}", clientId);
        });

        // 连接超时时移除连接
        emitter.onTimeout(() -> {
            emitterMap.remove(clientId);
            log.info("SSE 连接超时，clientId={}", clientId);
        });

        // 连接异常时移除连接
        emitter.onError((e) -> {
            emitterMap.remove(clientId);
            log.warn("SSE 连接异常，clientId={}", clientId, e);
        });

        // 建立连接后，发送一条连接成功消息
        sendToClient(
                clientId,
                "sse.connected",
                SseMessage.of("sse.connected", "SSE连接成功", clientId)
        );

        log.info("SSE 连接建立成功，clientId={}，当前连接数={}", clientId, emitterMap.size());

        return emitter;
    }

    /**
     * 广播 SSE 消息给所有客户端
     *
     * @param eventType 事件类型
     * @param message   消息说明
     * @param data      业务数据
     */
    public void broadcast(String eventType, String message, Object data) {
        // 构造统一 SSE 消息对象
        SseMessage<Object> sseMessage = SseMessage.of(eventType, message, data);

        // 遍历所有 SSE 连接并发送消息
        emitterMap.forEach((clientId, emitter) -> sendToClient(clientId, eventType, sseMessage));
    }

    /**
     * 向指定客户端发送 SSE 消息
     *
     * @param clientId  客户端连接ID
     * @param eventName SSE 事件名称
     * @param data      需要发送的数据对象
     */
    private void sendToClient(String clientId, String eventName, Object data) {
        // 根据客户端ID获取 SSE 连接
        SseEmitter emitter = emitterMap.get(clientId);

        if (emitter == null) {
            return;
        }

        try {
            // 将 Java 对象序列化为 JSON 字符串，避免中文乱码
            String jsonData = objectMapper.writeValueAsString(data);

            // 发送 SSE 事件
            emitter.send(
                    SseEmitter.event()
                            .name(eventName)
                            .data(jsonData, MediaType.APPLICATION_JSON)
            );
        } catch (JsonProcessingException e) {
            log.error("SSE 消息 JSON 序列化失败，clientId={}", clientId, e);
        } catch (IOException e) {
            // 发送失败说明客户端连接可能已经断开，移除该连接
            emitterMap.remove(clientId);
            log.warn("SSE 消息发送失败，已移除连接，clientId={}", clientId, e);
        }
    }

    /**
     * 定时发送 SSE 心跳
     *
     * 用于保持连接活跃，并方便开发阶段观察 SSE 是否正常工作。
     */
    @Scheduled(fixedDelay = 15000)
    public void sendHeartbeat() {
        if (emitterMap.isEmpty()) {
            return;
        }

        // 广播心跳消息
        broadcast("heartbeat", "SSE心跳检测", Map.of("onlineCount", emitterMap.size()));
    }

    /**
     * 获取当前 SSE 在线连接数量
     *
     * @return 当前在线连接数量
     */
    public int getOnlineCount() {
        return emitterMap.size();
    }
}