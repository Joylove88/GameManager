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
 * 战斗结果奖励领取
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "战斗结果奖励领取")
public class FightClaimReq {
    /**
     * 战斗记录ID
     */
    @ApiModelProperty(value = "战斗记录ID")
    private Long combatId;

}
