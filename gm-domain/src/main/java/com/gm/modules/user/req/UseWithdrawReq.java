package com.gm.modules.user.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 玩家提现请求参数
 */
@Data
public class UseWithdrawReq {
    /**
     * 扣gas费交易哈希
     */
    @NotBlank(message = "refundHash can not null")
    private String refundHash;
    /**
     * 提现类型(0:战斗账户，1：代理账户)
     */
    @NotBlank(message = "withdraw type must 0 or 1")
    private String withdrawType;
    /**
     * 提现金额
     */
    private BigDecimal withdrawMoney;
    /**
     * 提现申请扣除gas费
     */
    private BigDecimal applyWithdrawGas;

}
