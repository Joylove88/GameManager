package com.gm.modules.basicconfig.dto;

import lombok.Data;

/**
 * Created by Axiang on 2022/6/6 0006.
 */
@Data
public class AttributeEntity {
    /**
     * 英雄名称
     */
    private String heroName;
    /**
     * 英雄等级
     */
    private Long heroLevel;
    /**
     * 英雄星级
     */
    private Integer heroStar;
    /**
     * 技能名称
     */
    private String skillName;
    /**
     * 伤害星级
     */
    private Long skillStarLevel;
    /**
     * 技能类型（0：输出，1：辅助恢复，2：辅助加成）
     */
    private String skillType;
    /**
     * 技能位置
     */
    private Integer skillSolt;
    /**
     * 固定伤害
     */
    private Double skillFixedDamage;
    /**
     * 英雄属性伤害加成
     */
    private Double skillDamageBonusHero;
    /**
     * 装备属性伤害加成
     */
    private Double skillDamageBonusEquip;
    /**
     * 技能描述
     */
    private String skillDescription;
    /**
     * 最大生命值
     */
    private Double MaxHp;
    /**
     * 加成的生命
     */
    private Double addMaxHp;
    /**
     * 初始生命值
     */
    private Double hp;
    /**
     * 成长属性-生命值
     */
    private Double growHp;
    /**
     * 初始法力值
     */
    private Double mp;
    /**
     * 最大法力值
     */
    private Double MaxMp;
    /**
     * 成长属性-法力值
     */
    private Double growMp;
    /**
     * 初始生命值恢复
     */
    private Double hpRegen;
    /**
     * 成长属性-生命值恢复
     */
    private Double growHpRegen;
    /**
     * 初始法力值恢复
     */
    private Double mpRegen;
    /**
     * 成长属性-法力值恢复
     */
    private Double growMpRegen;
    /**
     * 初始护甲
     */
    private Double armor;
    /**
     * 成长属性-护甲
     */
    private Double growArmor;
    /**
     * 初始魔抗
     */
    private Double magicResist;
    /**
     * 成长属性-魔抗
     */
    private Double growMagicResist;
    /**
     * 初始攻击力
     */
    private Double attackDamage;
    /**
     * 成长属性-攻击力
     */
    private Double growAttackDamage;
    /**
     * 初始法攻
     */
    private Double attackSpell;
    /**
     * 成长属性-法攻
     */
    private Double growAttackSpell;


    /**
     * 装备属性-生命值
     */
    private Double equipHealth;
    /**
     * 装备属性-法力值
     */
    private Double equipMana;
    /**
     * 装备属性-生命值恢复
     */
    private double equipHealthRegen;
    /**
     * 装备属性-法力值恢复
     */
    private double equipManaRegen;
    /**
     * 装备属性-护甲
     */
    private Double equipArmor;
    /**
     * 装备属性-魔抗
     */
    private Double equipMagicResist;
    /**
     * 装备属性-攻击力
     */
    private Double equipAttackDamage;
    /**
     * 装备属性-法攻
     */
    private Double equipAttackSpell;
}
