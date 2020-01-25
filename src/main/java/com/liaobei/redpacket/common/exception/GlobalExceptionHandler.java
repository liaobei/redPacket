package com.liaobei.redpacket.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.liaobei.redpacket.common.response.BaseResponse;
import com.liaobei.redpacket.common.response.MessageResponse;

/**
 * @Author: liaobei
 */

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RedPacketException.class)
    @Nullable
    public final BaseResponse handleRedPacketException(Exception ex, HttpServletRequest request) throws Exception {
        BaseResponse response = new MessageResponse();
        response.setCode(001);
        response.setMsg(ex.getMessage());
        return response;
    }
}
