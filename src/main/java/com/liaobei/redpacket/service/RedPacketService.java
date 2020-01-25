package com.liaobei.redpacket.service;

import com.liaobei.redpacket.common.request.DispatchRequest;
import com.liaobei.redpacket.common.request.GrabRequest;
import com.liaobei.redpacket.common.response.BaseResponse;

/**
 * @Author: liaobei
 */
public interface RedPacketService {
    /**
     * 发红包
     * 
     * @param request
     */
    BaseResponse dispatch(DispatchRequest request);

    /**
     * 抢红包
     * @param request
     * @return
     */
    BaseResponse grab(GrabRequest request);
}
