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

}
