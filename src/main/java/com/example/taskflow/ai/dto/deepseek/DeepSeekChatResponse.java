package com.example.taskflow.ai.dto.deepseek;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeepSeekChatResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 模型输出候选列表
     */
    private List<Choice> choices;

    /**
     * token 使用情况
     */
    private Usage usage;

    @Data
    public static class Choice {

        /**
         * 第几个候选结果
         */
        private Integer index;

        /**
         * 模型返回消息
         */
        private Message message;

        /**
         * 结束原因
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    public static class Message {

        /**
         * 角色，一般是 assistant
         */
        private String role;

        /**
         * 模型最终回答内容
         */
        private String content;

        /**
         * 推理模型可能返回的推理内容字段
         *
         * 当前业务不需要展示推理过程，可以不使用。
         */
        @JsonProperty("reasoning_content")
        private String reasoningContent;
    }

    @Data
    public static class Usage {

        /**
         * 输入 token 数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 输出 token 数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 总 token 数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}