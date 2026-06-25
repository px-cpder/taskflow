package com.example.taskflow.ai.dto.deepseek;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DeepSeekChatRequest {

    /**
     * 模型名称，例如 deepseek-v4-flash
     */
    private String model;

    /**
     * 对话消息列表
     */
    private List<Message> messages;

    /**
     * 是否流式输出
     *
     * 当前后端 AI 处理是同步入库，所以先设置 false。
     */
    private Boolean stream;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大输出 token 数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 额外参数
     *
     * DeepSeek 新模型支持 thinking 配置。
     * 如果你不想开启推理模式，可以不传。
     */
    private Map<String, Object> thinking;

    /**
     * 快速构建普通非流式请求
     *
     * @param model       模型名称
     * @param prompt      用户提示词
     * @param temperature 温度
     * @param maxTokens   最大输出 token
     * @return DeepSeek 请求对象
     */
    public static DeepSeekChatRequest of(String model, String prompt, Double temperature, Integer maxTokens) {
        DeepSeekChatRequest request = new DeepSeekChatRequest();

        request.setModel(model);
        request.setStream(false);
        request.setTemperature(temperature);
        request.setMaxTokens(maxTokens);

        request.setMessages(List.of(
                new Message("system", "你是 TaskFlow 智能任务协作平台的 AI 助手，请用简洁、清晰、可执行的中文回答。"),
                new Message("user", prompt)
        ));

        return request;
    }

    @Data
    public static class Message {

        /**
         * 消息角色：
         * system / user / assistant
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}