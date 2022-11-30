package com.gm.modules.user.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 玩家提现请求参数
 */
@Data
public class UseWithdrawReq {
    /**
     * 提现金额
     */
    @NotBlank(message = "withdraw money can not null")
    private String withdrawMoney;
    /**
     * 提现类型(0:战斗账户，1：代理账户)
     */
    @NotBlank(message = "withdraw type must 0 or 1")
    private String withdrawType;
    /**
     * 提现手续费
     */
    private Double withdrawHandlingFee;

}
