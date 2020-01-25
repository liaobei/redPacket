package com.liaobei.redpacket.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: liaobei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    /**
     * redis suffix lock key
     */
    String sufKey() default "redisLock";

    /**
     * key description
     */
    String keyDesc() default "";

    /**
     * error message
     */
    String errorMessage() default "请稍后重试";

    long expireTime() default 60;

    /**
     * 是否使用主机ip作为key的一部分
     */
    boolean useHostIp() default false;
}
