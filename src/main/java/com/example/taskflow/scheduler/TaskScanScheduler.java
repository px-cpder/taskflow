package com.example.taskflow.scheduler;

import com.example.taskflow.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskScanScheduler {

    /**
     * 任务业务服务，用于执行逾期任务扫描和状态更新
     */
    private final TaskService taskService;

    /**
     * 每次最多扫描处理的任务数量
     *
     * <p>
     * 从配置文件 taskflow.task.batch-size 读取；
     * 如果没有配置，默认值为 100。
     * </p>
     */
    @Value("${taskflow.task.batch-size:100}")
    private Integer batchSize;

    /**
     * 定时扫描逾期任务
     *
     * <p>
     * fixedDelayString 表示上一次任务执行完成后，间隔指定时间再次执行。
     * 这里从配置文件 taskflow.task.scan-delay-ms 读取扫描间隔；
     * 如果没有配置，默认每 5000 毫秒扫描一次。
     * </p>
     */
    @Scheduled(fixedDelayString = "${taskflow.task.scan-delay-ms:5000}")
    public void scanOverdueTasks() {
        try {
            // 执行逾期任务扫描，并返回本次处理数量
            int count = taskService.scanAndMarkOverdueTasks(batchSize);

            // 只有处理到逾期任务时才打印日志，避免控制台刷屏
            if (count > 0) {
                log.info("定时扫描逾期任务完成，本次处理任务数量：{}", count);
            }
        } catch (Exception e) {
            // 捕获异常，防止一次扫描失败影响后续定时任务继续执行
            log.error("定时扫描逾期任务失败", e);
        }
    }
}