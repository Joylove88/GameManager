package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-08-09 19:13:43
 */
@Data
@TableName("gm_user_withdraw")
public class GmUserWithdrawEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId
    private Long withdrawId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 提现金额
     */
    private BigDecimal withdrawMoney;
    /**
     * 提现状态（0：申请提现，1：提现成功，2：提现失败）
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建时间
     */
    private Long createTimeTs;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改时间
     */
    private Long updateTimeTs;
    /**
     * 提现交易哈希
     */
    private String withdrawHash;
    /**
     * 提现账户类型(0:战斗账户，1：代理账户)
     */
    private String currency;

}
