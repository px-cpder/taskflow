package com.example.taskflow.ai.service;

public interface LlmClient {

    /**
     * 调用大模型并返回文本结果
     *
     * 当前阶段先由 MockLlmClient 模拟返回；
     * 后续接入真实大模型时，只需要新增一个实现类即可。
     *
     * @param prompt 发送给大模型的提示词
     * @return 大模型返回的文本内容
     */
    String chat(String prompt);
}