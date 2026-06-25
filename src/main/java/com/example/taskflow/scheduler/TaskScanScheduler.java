package com.example.taskflow.scheduler;

import com.example.taskflow.common.lock.RedisLockService;
import com.example.taskflow.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskScanScheduler {

    /**
     * 任务业务服务，用于执行逾期任务扫描和状态更新
     */
    private final TaskService taskService;

    /**
     * Redis 分布式锁服务
     *
     * 用于保证多实例部署时，同一时间只有一个实例执行逾期任务扫描。
     */
    private final RedisLockService redisLockService;

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
     * 逾期任务扫描锁 key
     *
     * 多个后端实例必须使用同一个 lockKey，
     * 这样才能互斥执行定时扫描。
     */
    @Value("${taskflow.task.scan-lock-key:taskflow:lock:task-scan}")
    private String scanLockKey;

    /**
     * 扫描锁过期时间，单位毫秒
     *
     * 作用：
     * 如果某个实例获取锁后宕机，Redis 会在 TTL 到期后自动释放锁，
     * 避免后续扫描任务永久无法执行。
     */
    @Value("${taskflow.task.scan-lock-ttl-ms:30000}")
    private Long scanLockTtlMs;

    /**
     * 当前应用实例ID
     *
     * 用于区分不同后端实例。
     * 每次应用启动都会生成一个新的 instanceId。
     */
    private final String instanceId = UUID.randomUUID().toString();


    /**
     * 定时扫描逾期任务
     *
     * 执行流程：
     * 1. 生成本次扫描的 lockValue
     * 2. 尝试获取 Redis 分布式锁
     * 3. 获取锁失败则跳过本次扫描
     * 4. 获取锁成功则执行逾期任务扫描
     * 5. 扫描完成后释放锁
     */
    @Scheduled(fixedDelayString = "${taskflow.task.scan-delay-ms:5000}")
    public void scanOverdueTasks() {
        // 本次锁 value，包含实例ID和随机ID，用于释放锁时校验锁归属
        String lockValue = instanceId + ":" + UUID.randomUUID();

        // 锁过期时间
        Duration lockTtl = Duration.ofMillis(scanLockTtlMs);

        // 尝试获取 Redis 分布式锁
        boolean locked = redisLockService.tryLock(scanLockKey, lockValue, lockTtl);

        if (!locked) {
            log.debug("当前实例未获取到逾期任务扫描锁，跳过本次扫描，instanceId={}", instanceId);
            return;
        }

        try {
            log.info("当前实例获取到逾期任务扫描锁，开始扫描，instanceId={}", instanceId);

            // 执行逾期任务扫描，返回本次处理数量
            int count = taskService.scanAndMarkOverdueTasks(batchSize);

            log.info("定时扫描逾期任务完成，本次处理任务数量：{}", count);
        } catch (Exception e) {
            log.error("定时扫描逾期任务失败", e);
        } finally {
            // 释放锁时会校验 lockValue，避免误删其他实例的锁
            boolean unlocked = redisLockService.unlock(scanLockKey, lockValue);

            log.debug("释放逾期任务扫描锁结果：{}，instanceId={}", unlocked, instanceId);
        }
    }
}