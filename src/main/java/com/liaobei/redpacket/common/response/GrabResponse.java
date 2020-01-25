package com.liaobei.redpacket.common.response;

import lombok.Data;

/**
 * @Author: liaobei
 */
@Data
public class GrabResponse extends BaseResponse {
    private Double count;

    public static GrabResponse success(Double count) {
        GrabResponse grabResponse = new GrabResponse();
        grabResponse.setCount(count);
        grabResponse.setCode(200);
        grabResponse.setMsg("成功");
        return grabResponse;
    }
}
