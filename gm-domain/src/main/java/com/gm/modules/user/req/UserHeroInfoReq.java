package com.gm.modules.user.req;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoReq {
    /**
     * ID
     */
    private Long userHeroId;
    /**
     * 英雄名称
     */
    private String heroName;
    /**
     * 玩家装备ID
     */
    private Long userEquipmentId;
    /**
     * 父子级装备链
     */
    private String parentEquipChain;

    /**
     * 战斗力（后台获取矿工比例用）
     */
    private BigDecimal combatPower;
}
