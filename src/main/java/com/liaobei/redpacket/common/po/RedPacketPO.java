package com.liaobei.redpacket.common.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @Author: liaobei
 */
@TableName("RedPacket")
@Data
public class RedPacketPO implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long Id;
    @TableField("userId")
    private Long userId;
    private Integer count;
    @TableField("grabCount")
    private Integer grabCount;
    private Double total;
    @TableField("grabTotal")
    private Double grabTotal;
    private String remark;
    private Boolean valid;
    @TableField("createTime")
    private Date createTime;
}
