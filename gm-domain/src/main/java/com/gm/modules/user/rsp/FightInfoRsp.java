/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 战斗信息
 *
 * @author Axiang
 */
@Data
@ApiModel(value = "战斗信息")
public class FightInfoRsp {
    /**
     * 战斗记录ID
     */
    @ApiModelProperty(value = "战斗记录ID")
    private Long combatId;
    /**
     * 队伍ID
     */
    @ApiModelProperty(value = "队伍ID")
    private Long teamId;
    /**
     * 战斗开始时间
     */
    private Long startTimeTs;
    /**
     * 战斗结束倒计时
     */
    private Long endSec;
    /**
     * 固定时间格式
     */
    private String countdown;

    /**
     * 战斗过程
     */
    private List<BattleDetailsRsp> battleDetails = null;


}
