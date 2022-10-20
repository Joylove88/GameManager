/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.basicconfig.rsp;

import com.gm.modules.user.rsp.UserHeroInfoRsp;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 队伍信息
 *
 * @author Axiang Axiang@gmail.com
 */
@Data
public class TeamInfoRsp {
    /**
     * 主ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 副本ID
     */
    private Long dungeonId;
    /**
     * 战斗记录ID
     */
    private Long combatId;
    /**
     * 队伍名称
     */
    private String teamName;
    /**
     * 队伍顺序
     */
    private Integer teamSolt;
    /**
     * 队伍战力
     */
    private Long teamPower;
    /**
     * 队伍矿工数
     */
    private BigDecimal teamMinter;
    /**
     * 战斗状态（0：未战斗，1：战斗中，2：战斗结束）
     */
    private String status;
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
    /**
     * 战斗结束时间
     */
    private Long endTimeTs;
    /**
     * 战斗倒计时
     */
    private Long endSec;
    /**
     * 固定时间格式
     */
    private String countdown;

    /**
     * 英雄集合
     */
    private List<UserHeroInfoRsp> userHeroInfoRsps = null;

}
