package com.example.taskflow.ai.service.impl;

import com.example.taskflow.ai.service.LlmClient;
import org.springframework.stereotype.Service;

@Service
public class MockLlmClient implements LlmClient {

    /**
     * 模拟大模型调用
     *
     * 当前阶段不真正请求外部 AI 平台，而是根据 prompt 内容返回固定结果，
     * 这样可以先把 AI 记录保存、任务总结回填、SSE 推送等后端流程跑通。
     *
     * @param prompt 提示词
     * @return 模拟的大模型响应
     */
    @Override
    public String chat(String prompt) {
        if (prompt.contains("任务拆解")) {
            return """
                    1. 明确任务目标和验收标准。
                    2. 拆分后端接口、数据库、前端页面等子任务。
                    3. 优先完成核心业务流程。
                    4. 完成测试和异常情况处理。
                    5. 整理接口文档和项目说明。
                    """;
        }

        if (prompt.contains("任务总结")) {
            return """
                    该任务主要围绕当前需求进行实现，需要完成核心功能开发、接口测试和结果验证。
                    建议优先保证主流程可用，再逐步补充异常处理、日志记录和实时通知能力。
                    """;
        }

        if (prompt.contains("风险分析")) {
            return """
                    风险等级：中等。
                    主要风险包括截止时间不足、需求范围变化、接口联调失败以及异常场景遗漏。
                    建议提前完成核心接口测试，并预留时间处理边界条件。
                    """;
        }

        return "这是模拟大模型返回结果。";
    }
}