package com.gm.modules.user.req;

import lombok.Data;

/**
 * 玩家提现请求参数
 */
@Data
public class UseWithdrawReq {
    /**
     * 提现金额
     */
    private String withdrawMoney;
    /**
     * 提现类型(0:战斗账户，1：代理账户)
     */
    private String withdrawType;
    /**
     * 提现手续费
     */
    private String withdrawHandlingFee;

}
