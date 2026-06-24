package com.example.taskflow.ai.service;

import com.example.taskflow.task.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    /**
     * 构建任务拆解 Prompt
     *
     * @param task 当前任务对象
     * @return 发送给大模型的提示词
     */
    public String buildSplitPrompt(Task task) {
        return """
                你是一个任务管理助手，请根据下面的任务信息进行任务拆解。

                要求：
                1. 拆解为清晰的执行步骤。
                2. 每个步骤要简洁可执行。
                3. 不要输出无关内容。

                任务标题：%s
                任务描述：%s
                优先级：%s
                截止时间：%s
                """.formatted(
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getDeadline()
        );
    }

    /**
     * 构建任务总结 Prompt
     *
     * @param task 当前任务对象
     * @return 发送给大模型的提示词
     */
    public String buildSummaryPrompt(Task task) {
        return """
                你是一个任务总结助手，请根据下面的任务信息生成任务总结。

                要求：
                1. 总结任务目标。
                2. 提炼任务重点。
                3. 给出简洁的执行建议。
                4. 控制在 200 字以内。

                任务标题：%s
                任务描述：%s
                当前状态：%s
                优先级：%s
                负责人：%s
                截止时间：%s
                """.formatted(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getAssigneeName(),
                task.getDeadline()
        );
    }

    /**
     * 构建任务风险分析 Prompt
     *
     * @param task 当前任务对象
     * @return 发送给大模型的提示词
     */
    public String buildRiskPrompt(Task task) {
        return """
                你是一个项目风险分析助手，请根据下面的任务信息分析延期风险。

                要求：
                1. 判断风险等级：低 / 中 / 高。
                2. 说明主要风险点。
                3. 给出规避建议。
                4. 输出内容要清晰简洁。

                任务标题：%s
                任务描述：%s
                当前状态：%s
                优先级：%s
                负责人：%s
                截止时间：%s
                """.formatted(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getAssigneeName(),
                task.getDeadline()
        );
    }
}