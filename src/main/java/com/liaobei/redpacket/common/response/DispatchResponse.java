package com.liaobei.redpacket.common.response;

import lombok.Data;

/**
 * @Author: liaobei
 */
@Data
public class DispatchResponse extends BaseResponse {
    private Long redPacketId;

    public static DispatchResponse success(Long redPacketId) {
        DispatchResponse response = new DispatchResponse();
        response.setCode(200);
        response.setMsg("发送成功");
        response.setRedPacketId(redPacketId);
        return response;
    }
}
