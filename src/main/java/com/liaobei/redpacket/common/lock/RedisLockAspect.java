package com.liaobei.redpacket.common.lock;

import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liaobei.redpacket.common.exception.RedPacketException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: liaobei
 */
@Aspect
@Component
@Slf4j
public class RedisLockAspect {
    @Autowired
    private RedisLockHelper redisLockHelper;

    @Pointcut(value = "@annotation(redisLock)")
    public void pointCut(RedisLock redisLock) {}

    @Before(value = "pointCut(redisLock)", argNames = "redisLock")
    public void doBefore(RedisLock redisLock) {
        String key = this.getRedisKey(redisLock);

        log.info("[" + redisLock.keyDesc() + "]获取redis锁[" + key + "]");

        if (!redisLockHelper.lock(key, redisLock.expireTime())) {
            log.info("[" + redisLock.keyDesc() + "]获取redis锁[" + key + "]失败");
            throw new RedPacketException(redisLock.errorMessage());
        }
    }

    @AfterReturning(value = "pointCut(redisLock)", argNames = "redisLock")
    public void doAfter(RedisLock redisLock) {
        String key = this.getRedisKey(redisLock);

        redisLockHelper.unlock(key);
        log.info("[" + redisLock.keyDesc() + "]释放redis锁[" + key + "]");
    }

    @AfterThrowing(value = "pointCut(redisLock)", argNames = "redisLock")
    public void afterThrowing(RedisLock redisLock) {
        String key = this.getRedisKey(redisLock);

        redisLockHelper.unlock(key);
        log.info("[" + redisLock.keyDesc() + "]释放redis锁[" + key + "]");
    }

    private String getRedisKey(RedisLock redisLock) {
        return redisLock.sufKey();
    }



}
