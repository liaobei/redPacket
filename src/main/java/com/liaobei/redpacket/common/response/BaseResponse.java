package com.liaobei.redpacket.common.response;

import lombok.Data;

/**
 * @Author: liaobei
 */
@Data
public abstract class BaseResponse {
    private String msg;
    private Integer code;


}
