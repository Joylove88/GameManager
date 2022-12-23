/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽奖参数
 *
 * @author Axiang
 */
@Data
@ApiModel(value = "抽奖参数")
public class SummonReq {
    /**
     * 交易hash
     */
    @ApiModelProperty(value = "交易hash")
    private String transactionHash;
    /**
     * 召唤次数
     */
    @ApiModelProperty(value = "召唤次数")
    private Integer summonNum;
    /**
     * 召唤类型('1':英雄，'2':装备，'3':药水)
     */
    private String summonType;
    /**
     * 货币类型0:金币，1:加密货币
     */
    @ApiModelProperty(value = "货币类型")
    private String curType;

    /**
     * 订单金额
     */
    private BigDecimal orderFee;

    /**
     * 实际支付金额
     */
    private BigDecimal realFee;

    /**
     * NFT_TOKENID
     */
    private List<String> tokenIds = new ArrayList<>();

}
