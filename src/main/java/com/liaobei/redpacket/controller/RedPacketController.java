package com.liaobei.redpacket.controller;

import javax.annotation.Resource;

import com.liaobei.redpacket.common.request.GrabRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.liaobei.redpacket.common.request.DispatchRequest;
import com.liaobei.redpacket.common.response.BaseResponse;
import com.liaobei.redpacket.common.utils.RedisUtils;
import com.liaobei.redpacket.service.RedPacketService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liaobei
 */
@RestController
@RequestMapping("redPacket")
public class RedPacketController {
    public static final String DISPATCH = "dispatch";
    public static final String GRAB = "grab";

    @Resource
    private RedPacketService redPacketService;
    @Resource
    private RedisUtils redisUtils;

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        redisUtils.set("test","test");
        return "test";
    }

    @GetMapping(DISPATCH)
    public BaseResponse dispatch(DispatchRequest request) {
        return redPacketService.dispatch(request);
    }

    @GetMapping(GRAB)
    public BaseResponse grab(GrabRequest request) {
        return redPacketService.grab(request);
    }

}
