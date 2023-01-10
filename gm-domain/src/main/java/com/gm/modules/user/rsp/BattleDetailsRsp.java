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

/**
 * 战斗详情
 *
 * @author Axiang
 */
@Data
@ApiModel(value = "战斗详情")
public class BattleDetailsRsp {

    /**
     * 等级
     */
    private Long level;

    /**
     * 名称
     */
    private String name;

    /**
     * 怪物等级
     */
    private Long mLevel;

    /**
     * 怪物名称
     */
    private String mName;

    /**
     * 星级
     */
    private Long starCode;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 造成伤害
     */
    private Double dealDamage;

    /**
     * 剩余血量
     */
    private Double HP;

    /**
     * 恢复血量
     */
    private Double HPRegen;

    /**
     * 战斗描述
     */
    private String BattleDescribe;

    /**
     * 类型：0怪物，1英雄
     */
    private String type;

    /**
     * 队伍战力
     */
    private Double teamPower;

    public BattleDetailsRsp(Long level, String name, Long mLevel, String mName, Long starCode, String skillName, Double dealDamage, Double HP, Double HPRegen, String battleDescribe, String type, Double teamPower) {
        this.level = level;
        this.name = name;
        this.mLevel = mLevel;
        this.mName = mName;
        this.starCode = starCode;
        this.skillName = skillName;
        this.dealDamage = dealDamage;
        this.HP = HP;
        this.HPRegen = HPRegen;
        BattleDescribe = battleDescribe;
        this.type = type;
        this.teamPower = teamPower;
    }
}
