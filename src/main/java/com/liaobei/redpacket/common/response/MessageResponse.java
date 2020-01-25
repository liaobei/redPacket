package com.liaobei.redpacket.common.response;

import lombok.Data;

/**
 * @Author: liaobei
 */
@Data
public class MessageResponse extends BaseResponse {

    public static MessageResponse success(String msg) {
        MessageResponse response = new MessageResponse();
        response.setCode(200);
        response.setMsg("发送成功");
        return response;
    }
}
