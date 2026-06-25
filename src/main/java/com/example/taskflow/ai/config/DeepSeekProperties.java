package com.example.taskflow.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "taskflow.ai.deepseek")
public class DeepSeekProperties {

    /**
     * DeepSeek API 基础地址
     *
     * 官方 OpenAI 兼容格式 base_url：
     * https://api.deepseek.com
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * DeepSeek API Key
     *
     * 建议通过环境变量 DEEPSEEK_API_KEY 注入，
     * 不要硬编码到代码或提交到 Git 仓库。
     */
    private String apiKey;

    /**
     * 使用的大模型名称
     *
     * 推荐先使用 deepseek-v4-flash，
     * 响应速度和成本更适合普通任务辅助场景。
     */
    private String model = "deepseek-v4-flash";

    /**
     * 采样温度
     *
     * 值越高，回答越发散；
     * 任务拆解、总结、风险分析建议 0.5 ~ 0.8。
     */
    private Double temperature = 0.7;

    /**
     * 最大输出 token 数
     *
     * 控制模型单次返回内容长度，防止输出过长导致费用增加。
     */
    private Integer maxTokens = 1200;
}