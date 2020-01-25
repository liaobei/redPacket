package com.liaobei.redpacket.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liaobei
 */
@Component
@Slf4j
public class RedisLockHelper {
    // key的TTL, 60分钟
    private static final int finalDefaultTTLwithKey = 3600;

    // 锁默认超时时间,20秒
    private static final long defaultExpireTime = 20 * 1000;

    private static final boolean Success = true;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 加锁,锁默认超时时间20秒
     *
     * @param resource
     * @return
     */
    public boolean lock(String resource) {
        return this.lock(resource, defaultExpireTime);
    }

    /**
     * 加锁,同时设置锁超时时间
     *
     * @param key 分布式锁的key
     * @param expireTime 单位是ms
     * @return
     */
    public boolean lock(String key, long expireTime) {

        log.debug("redis lock debug, start. key:[{}], expireTime:[{}]", key, expireTime);
        long now = Instant.now().toEpochMilli();
        long lockExpireTime = now + expireTime;

        // setnx
        boolean executeResult = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(lockExpireTime));
        log.debug("redis lock debug, setnx. key:[{}], expireTime:[{}], executeResult:[{}]", key, expireTime,
                executeResult);

        // 取锁成功,为key设置expire
        if (executeResult == Success) {
            redisTemplate.expire(key, finalDefaultTTLwithKey, TimeUnit.SECONDS);
            return true;
        }
        // 没有取到锁,继续流程
        else {
            Object valueFromRedis = this.getKeyWithRetry(key, 3);
            // 避免获取锁失败,同时对方释放锁后,造成NPE
            if (valueFromRedis != null) {
                // 已存在的锁超时时间
                long oldExpireTime = Long.parseLong((String)valueFromRedis);
                log.debug("redis lock debug, key already seted. key:[{}], oldExpireTime:[{}]", key, oldExpireTime);
                // 锁过期时间小于当前时间,锁已经超时,重新取锁
                if (oldExpireTime <= now) {
                    log.debug("redis lock debug, lock time expired. key:[{}], oldExpireTime:[{}], now:[{}]", key,
                            oldExpireTime, now);
                    String valueFromRedis2 = redisTemplate.opsForValue().getAndSet(key, String.valueOf(lockExpireTime));
                    long currentExpireTime = Long.parseLong(valueFromRedis2);
                    // 判断currentExpireTime与oldExpireTime是否相等
                    if (currentExpireTime == oldExpireTime) {
                        // 相等,则取锁成功
                        log.debug(
                                "redis lock debug, getSet. key:[{}], currentExpireTime:[{}], oldExpireTime:[{}], lockExpireTime:[{}]",
                                key, currentExpireTime, oldExpireTime, lockExpireTime);
                        redisTemplate.expire(key, finalDefaultTTLwithKey, TimeUnit.SECONDS);
                        return true;
                    } else {
                        // 不相等,取锁失败
                        return false;
                    }
                }
            } else {
                log.warn("redis lock,lock have been release. key:[{}]", key);
                return false;
            }
        }
        return false;
    }

    private Object getKeyWithRetry(String key, int retryTimes) {
        int failTime = 0;
        while (failTime < retryTimes) {
            try {
                return redisTemplate.opsForValue().get(key);
            } catch (Exception e) {
                failTime++;
                if (failTime >= retryTimes) {
                    throw e;
                }
            }
        }
        return null;
    }

    /**
     * 解锁
     *
     * @param key
     * @return
     */
    public boolean unlock(String key) {
        log.debug("redis unlock debug, start. resource:[{}].", key);
        redisTemplate.delete(key);
        return Success;
    }
}
