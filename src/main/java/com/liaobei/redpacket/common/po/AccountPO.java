package com.liaobei.redpacket.common.po;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @Author: liaobei
 */
@TableName("Account")
@Data
public class AccountPO {
    @TableId("userId")
    private Long userId;
    private Double balance;
    @TableField("createTime")
    private Date createTime;
    @TableField("updateTime")
    private Date updateTime;
}
