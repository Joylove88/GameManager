/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.rsp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 战斗结果奖励领取
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "战斗结果奖励领取")
public class FightClaimRsp {
    /**
     * 获得的金币
     */
    private BigDecimal getGoldCoins;

    /**
     * 获得的装备
     */
    private String getEquip;

    /**
     * 获得的道具
     */
    private String getProps;

    /**
     * 获得的英雄经验
     */
    private Long getExp;

    /**
     * 获得的用户经验
     */
    private Long getUserExp;

    /**
     * 战斗结果状态（0：YOULOSE，1：YOUWIN，2：战斗中）
     */
    private String status;

    /**
     * 获得的装备集合
     */
    private List<UserEquipInfoRsp> userEquipInfoRsps = new ArrayList<>();

    /**
     * 英雄集合
     */
    private List<UserHeroInfoRsp> userHeroInfoRsps = new ArrayList<>();

}
