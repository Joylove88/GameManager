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

/**
 * 抽奖参数
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "抽奖参数")
public class DrawForm {
    /**
     * 交易hash
     */
    @ApiModelProperty(value = "交易hash")
    private String transactionHash;
    /**
     * 抽奖类型('1':单抽，'2':十连抽)
     */
    @ApiModelProperty(value = "抽奖类型")
    private String drawType;
    /**
     * 货币类型0:金币，1:加密货币
     */
    @ApiModelProperty(value = "货币类型")
    private String curType;
    /**
     * 物品类型('1':英雄，'2':装备，'3':药水)
     */
    private String itemType;

}
