package com.example.taskflow.ai.service.impl;

import com.example.taskflow.ai.dto.AiProcessRecordResponse;
import com.example.taskflow.ai.entity.AiProcessRecord;
import com.example.taskflow.ai.mapper.AiProcessRecordMapper;
import com.example.taskflow.ai.service.AiTaskService;
import com.example.taskflow.ai.service.LlmClient;
import com.example.taskflow.ai.service.PromptBuilder;
import com.example.taskflow.common.enums.AiProcessStatus;
import com.example.taskflow.common.enums.AiProcessType;
import com.example.taskflow.common.exception.BusinessException;
import com.example.taskflow.sse.service.SseService;
import com.example.taskflow.task.entity.Task;
import com.example.taskflow.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiTaskServiceImpl implements AiTaskService {

    /**
     * 任务表 Mapper
     *
     * 用于查询任务信息，以及在 AI 总结时回填 task.ai_summary。
     */
    private final TaskMapper taskMapper;

    /**
     * AI 处理记录表 Mapper
     *
     * 用于保存和更新 ai_process_record 表。
     */
    private final AiProcessRecordMapper aiProcessRecordMapper;

    /**
     * Prompt 构造器
     *
     * 用于根据任务信息构造不同类型的提示词。
     */
    private final PromptBuilder promptBuilder;

    /**
     * 大模型客户端
     *
     * 当前注入的是 MockLlmClient；
     * 后续可替换为 DeepSeekLlmClient / DashScopeLlmClient。
     */
    private final LlmClient llmClient;

    /**
     * SSE 推送服务
     *
     * 用于 AI 处理完成或失败后向前端推送实时消息。
     */
    private final SseService sseService;

    /**
     * AI 拆解任务
     *
     * @param taskId 任务ID
     * @return AI 处理记录
     */
    @Override
    public AiProcessRecordResponse splitTask(Long taskId) {
        return processTask(taskId, AiProcessType.SPLIT);
    }

    /**
     * AI 总结任务
     *
     * @param taskId 任务ID
     * @return AI 处理记录
     */
    @Override
    public AiProcessRecordResponse summaryTask(Long taskId) {
        return processTask(taskId, AiProcessType.SUMMARY);
    }

    /**
     * AI 分析任务风险
     *
     * @param taskId 任务ID
     * @return AI 处理记录
     */
    @Override
    public AiProcessRecordResponse riskTask(Long taskId) {
        return processTask(taskId, AiProcessType.RISK);
    }

    /**
     * 查询 AI 处理记录
     *
     * @param recordId AI处理记录ID
     * @return AI 处理记录
     */
    @Override
    public AiProcessRecordResponse getRecordById(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("AI处理记录ID不能为空");
        }

        // 根据记录ID查询 AI 处理记录
        AiProcessRecord record = aiProcessRecordMapper.selectById(recordId);

        if (record == null) {
            throw new BusinessException("AI处理记录不存在");
        }

        return convertToResponse(record);
    }

    /**
     * 统一处理 AI 任务
     *
     * <p>
     * 该方法负责 AI 处理的完整闭环：
     * 1. 查询任务
     * 2. 构造 Prompt
     * 3. 保存 PROCESSING 记录
     * 4. 调用大模型
     * 5. 更新 COMPLETED / FAILED 状态
     * 6. 通过 SSE 推送处理结果
     * </p>
     *
     * @param taskId 任务ID
     * @param type   AI处理类型
     * @return AI处理记录
     */
    @Transactional(rollbackFor = Exception.class)
    public AiProcessRecordResponse processTask(Long taskId, AiProcessType type) {
        if (taskId == null) {
            throw new BusinessException("任务ID不能为空");
        }

        // 查询当前任务
        Task task = taskMapper.selectById(taskId);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 根据 AI 处理类型构造不同的提示词
        String prompt = buildPrompt(task, type);

        // 创建 AI 处理记录，初始状态为 PROCESSING
        AiProcessRecord record = new AiProcessRecord();
        record.setTaskId(taskId);
        record.setType(type.name());
        record.setStatus(AiProcessStatus.PROCESSING.name());
        record.setPrompt(prompt);
        record.setResult(null);
        record.setErrorMessage(null);

        aiProcessRecordMapper.insert(record);

        try {
            // 调用大模型客户端，当前由 MockLlmClient 模拟返回
            String result = llmClient.chat(prompt);

            // 更新 AI 处理记录为成功状态
            record.setStatus(AiProcessStatus.COMPLETED.name());
            record.setResult(result);
            record.setErrorMessage(null);

            aiProcessRecordMapper.updateById(record);

            // 如果是任务总结，则将 AI 总结结果回填到 task.ai_summary
            if (type == AiProcessType.SUMMARY) {
                task.setAiSummary(result);
                taskMapper.updateById(task);
            }

            // 转换响应对象
            AiProcessRecordResponse response = convertToResponse(record);

            // 广播 AI 处理完成事件
            sseService.broadcast(
                    "ai.completed",
                    "AI处理完成",
                    response
            );

            return response;
        } catch (Exception e) {
            // 更新 AI 处理记录为失败状态
            record.setStatus(AiProcessStatus.FAILED.name());
            record.setErrorMessage(e.getMessage());

            aiProcessRecordMapper.updateById(record);

            // 广播 AI 处理失败事件
            sseService.broadcast(
                    "ai.failed",
                    "AI处理失败",
                    convertToResponse(record)
            );

            throw new BusinessException("AI处理失败：" + e.getMessage());
        }
    }

    /**
     * 根据 AI 处理类型构造 Prompt
     *
     * @param task 当前任务对象
     * @param type AI处理类型
     * @return 提示词
     */
    private String buildPrompt(Task task, AiProcessType type) {
        return switch (type) {
            case SPLIT -> promptBuilder.buildSplitPrompt(task);
            case SUMMARY -> promptBuilder.buildSummaryPrompt(task);
            case RISK -> promptBuilder.buildRiskPrompt(task);
            case PRIORITY -> throw new BusinessException("暂未实现优先级建议功能");
        };
    }

    /**
     * 将 AI 处理记录实体转换为响应 DTO
     *
     * @param record AI处理记录实体
     * @return AI处理记录响应对象
     */
    private AiProcessRecordResponse convertToResponse(AiProcessRecord record) {
        AiProcessRecordResponse response = new AiProcessRecordResponse();

        // 基础字段
        response.setId(record.getId());
        response.setTaskId(record.getTaskId());
        response.setType(record.getType());
        response.setStatus(record.getStatus());

        // AI 内容字段
        response.setPrompt(record.getPrompt());
        response.setResult(record.getResult());
        response.setErrorMessage(record.getErrorMessage());

        // 时间字段
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());

        return response;
    }
}