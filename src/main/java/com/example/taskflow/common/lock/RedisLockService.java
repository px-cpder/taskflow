package com.example.taskflow.common.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    /**
     * Redis 字符串操作模板
     *
     * 用于执行 setIfAbsent、Lua 脚本释放锁等 Redis 操作。
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试获取分布式锁
     *
     * 实现方式：
     * SET key value NX PX ttl
     *
     * key 不存在时才能设置成功；
     * 设置成功表示当前实例获得锁；
     * ttl 用于防止服务宕机后锁无法释放。
     *
     * @param lockKey   锁 key
     * @param lockValue 锁 value，一般使用当前实例唯一标识
     * @param ttl       锁过期时间
     * @return true 表示获取锁成功，false 表示获取锁失败
     */
    public boolean tryLock(String lockKey, String lockValue, Duration ttl) {
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, ttl);

        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放分布式锁
     *
     * 注意：
     * 不能直接 delete(lockKey)，否则可能误删其他实例新获取的锁。
     *
     * 正确做法：
     * 先判断 Redis 中 lockKey 对应的 value 是否等于当前实例的 lockValue；
     * 如果相等，说明锁仍然属于当前实例，可以删除；
     * 如果不相等，说明锁已经过期并被其他实例重新获取，不能删除。
     *
     * @param lockKey   锁 key
     * @param lockValue 当前实例持有锁时写入的 value
     * @return true 表示释放成功，false 表示锁不存在或锁不属于当前实例
     */
    public boolean unlock(String lockKey, String lockValue) {
        String luaScript = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        Long result = stringRedisTemplate.execute(
                redisScript,
                Collections.singletonList(lockKey),
                lockValue
        );

        return result != null && result > 0;
    }
}