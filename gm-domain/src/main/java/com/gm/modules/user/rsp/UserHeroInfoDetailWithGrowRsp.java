package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoDetailWithGrowRsp {
    /**
     * ID
     */
    private Long userHeroId;
    /**
     * 等级ID
     */
    private Long heroLevelId;
    /**
     * 英雄ID
     */
    private Long heroId;
    /**
     * 英雄名称
     */
    private String heroName;
    /**
     * 英雄职业
     */
    private String heroRole;
    /**
     * 英雄战力
     */
    private Long heroPower;
    /**
     * 玩家英雄的矿工兑换比例（增幅,削减）
     */
    private Double scale;
    /**
     * 基础英雄的矿工兑换比例（增幅,削减）
     */
    private Double scaleI;
    /**
     * 英雄星级
     */
    private Integer starCode;
    /**
     * 英雄等级编码
     */
    private Integer levelCode;
    /**
     * 成长率
     */
    private Double growthRate;
    /**
     * 晋级到下一级所需经验值
     */
    private Long promotionExperience;
    /**
     * 累计获得的经验
     */
    private Long experienceObtain;
    /**
     * 当前等级获取的经验值
     */
    private Long currentExp;
    /**
     * 英雄描述
     */
    private String heroDescription;
    /**
     * 生命值
     */
    private Long health;
    /**
     * 法力值
     */
    private Long mana;
    /**
     * 生命值恢复
     */
    private Double healthRegen;
    /**
     * 法力值恢复
     */
    private Double manaRegen;
    /**
     * 护甲
     */
    private Long armor;
    /**
     * 魔抗
     */
    private Long magicResist;
    /**
     * 攻击力
     */
    private Long attackDamage;
    /**
     * 法攻
     */
    private Long attackSpell;
    /**
     * 成长属性-生命值
     */
    private Long growHealth;
    /**
     * 成长属性-法力值
     */
    private Long growMana;
    /**
     * 成长属性-生命值恢复
     */
    private Double growHealthRegen;
    /**
     * 成长属性-法力值恢复
     */
    private Double growManaRegen;
    /**
     * 成长属性-护甲
     */
    private Long growArmor;
    /**
     * 成长属性-魔抗
     */
    private Long growMagicResist;
    /**
     * 成长属性-攻击力
     */
    private Long growAttackDamage;
    /**
     * 成长属性-法攻
     */
    private Long growAttackSpell;


}
