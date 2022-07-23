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
 * 战斗信息
 *
 * @author Axiang
 */
@Data
@ApiModel(value = "战斗信息")
public class FightInfoReq {
    /**
     * 战斗记录ID
     */
    private Long combatId;

    /**
     * 队伍ID
     */
    private Long teamId;

    /**
     * 副本ID
     */
    private Long dungeonId;

    /**
     * 玩家英雄1ID
     */
    private Long userHero1Id;
    /**
     * 玩家英雄2ID
     */
    private Long userHero2Id;
    /**
     * 玩家英雄3ID
     */
    private Long userHero3Id;
    /**
     * 玩家英雄4ID
     */
    private Long userHero4Id;
    /**
     * 玩家英雄5ID
     */
    private Long userHero5Id;
}
