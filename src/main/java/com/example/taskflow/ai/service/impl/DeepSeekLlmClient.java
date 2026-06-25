package com.example.taskflow.ai.service.impl;

import com.example.taskflow.ai.config.DeepSeekProperties;
import com.example.taskflow.ai.dto.deepseek.DeepSeekChatRequest;
import com.example.taskflow.ai.dto.deepseek.DeepSeekChatResponse;
import com.example.taskflow.ai.service.LlmClient;
import com.example.taskflow.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "taskflow.ai", name = "provider", havingValue = "deepseek")
public class DeepSeekLlmClient implements LlmClient {

    /**
     * RestClient 构建器
     *
     * Spring Boot 3 推荐使用 RestClient 调用外部 HTTP 接口。
     */
    private final RestClient.Builder restClientBuilder;

    /**
     * DeepSeek 配置属性
     *
     * 包括 baseUrl、apiKey、model、temperature、maxTokens。
     */
    private final DeepSeekProperties properties;

    /**
     * 调用 DeepSeek Chat Completions 接口
     *
     * @param prompt 提示词
     * @return 大模型返回的文本内容
     */
    @Override
    public String chat(String prompt) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new BusinessException("DeepSeek API Key 未配置，请设置环境变量 DEEPSEEK_API_KEY");
        }

        if (!StringUtils.hasText(prompt)) {
            throw new BusinessException("提示词不能为空");
        }

        // 构建请求体
        DeepSeekChatRequest request = DeepSeekChatRequest.of(
                properties.getModel(),
                prompt,
                properties.getTemperature(),
                properties.getMaxTokens()
        );

        // 构建 RestClient
        RestClient restClient = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            // 调用 DeepSeek OpenAI 兼容接口：POST /chat/completions
            DeepSeekChatResponse response = restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(DeepSeekChatResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new BusinessException("DeepSeek 返回结果为空");
            }

            DeepSeekChatResponse.Message message = response.getChoices().get(0).getMessage();

            if (message == null || !StringUtils.hasText(message.getContent())) {
                throw new BusinessException("DeepSeek 返回内容为空");
            }

            if (response.getUsage() != null) {
                log.info("DeepSeek 调用完成，model={}，promptTokens={}，completionTokens={}，totalTokens={}",
                        response.getModel(),
                        response.getUsage().getPromptTokens(),
                        response.getUsage().getCompletionTokens(),
                        response.getUsage().getTotalTokens());
            }

            return message.getContent();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败", e);
            throw new BusinessException("调用 DeepSeek API 失败：" + e.getMessage());
        }
    }
}