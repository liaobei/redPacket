package com.liaobei.redpacket.common.exception;

/**
 * @Author: liaobei
 */
public class RedPacketException extends RuntimeException {
    public RedPacketException() {
        super();
    }

    public RedPacketException(String message) {
        super(message);
    }

    public RedPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedPacketException(Throwable cause) {
        super(cause);
    }

    protected RedPacketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
