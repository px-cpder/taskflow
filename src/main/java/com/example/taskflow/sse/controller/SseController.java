package com.example.taskflow.sse.controller;

import com.example.taskflow.common.result.ApiResult;
import com.example.taskflow.sse.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;

@Tag(name = "SSE实时推送接口", description = "提供 SSE 长连接建立、心跳检测和在线连接数量查询功能")
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    /**
     * SSE 服务类
     *
     * 用于创建 SSE 长连接、广播消息、查询在线连接数量。
     */
    private final SseService sseService;

    /**
     * 建立 SSE 长连接
     *
     * 这里重点指定 text/event-stream 和 UTF-8，
     * 用于解决 SSE 推送中文内容乱码问题。
     *
     * @param response 原始 HTTP 响应对象，用于设置编码和响应头
     * @return SSE 长连接对象
     */
    @Operation(summary = "建立SSE连接")
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(HttpServletResponse response) {
        // 设置响应字符编码为 UTF-8，防止中文乱码
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 设置 SSE 响应类型，并明确指定 charset=UTF-8
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8");

        // 禁止缓存，避免浏览器或代理缓存 SSE 数据
        response.setHeader("Cache-Control", "no-cache");

        // 禁止 Nginx 对 SSE 响应进行缓冲
        response.setHeader("X-Accel-Buffering", "no");

        return sseService.connect();
    }

    /**
     * 查询当前 SSE 在线连接数量
     *
     * @return 当前在线连接数量
     */
    @Operation(summary = "查询SSE在线连接数量")
    @GetMapping("/count")
    public ApiResult<Integer> getOnlineCount() {
        return ApiResult.success(sseService.getOnlineCount());
    }
}