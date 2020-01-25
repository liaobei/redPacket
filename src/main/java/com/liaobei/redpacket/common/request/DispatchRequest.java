package com.liaobei.redpacket.common.request;

import lombok.Data;

/**
 * @Author: liaobei
 */
@Data
public class DispatchRequest {
    private Long userId;
    private Double total;
    private Integer count;
    private String remark;
}
