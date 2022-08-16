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
    private Long heroStar;
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
    private Long MaxHp;
    /**
     * 加成的生命
     */
    private Long addMaxHp;
    /**
     * 初始生命值
     */
    private Long hp;
    /**
     * 成长属性-生命值
     */
    private Long growHp;
    /**
     * 初始法力值
     */
    private Long mp;
    /**
     * 最大法力值
     */
    private Long MaxMp;
    /**
     * 成长属性-法力值
     */
    private Long growMp;
    /**
     * 初始生命值恢复
     */
    private Long hpRegen;
    /**
     * 成长属性-生命值恢复
     */
    private Long growHpRegen;
    /**
     * 初始法力值恢复
     */
    private Long mpRegen;
    /**
     * 成长属性-法力值恢复
     */
    private Long growMpRegen;
    /**
     * 初始护甲
     */
    private Long armor;
    /**
     * 成长属性-护甲
     */
    private Long growArmor;
    /**
     * 初始魔抗
     */
    private Long magicResist;
    /**
     * 成长属性-魔抗
     */
    private Long growMagicResist;
    /**
     * 初始攻击力
     */
    private Long attackDamage;
    /**
     * 成长属性-攻击力
     */
    private Long growAttackDamage;
    /**
     * 初始法攻
     */
    private Long attackSpell;
    /**
     * 成长属性-法攻
     */
    private Long growAttackSpell;


    /**
     * 装备属性-生命值
     */
    private Long equipHealth;
    /**
     * 装备属性-法力值
     */
    private Long equipMana;
    /**
     * 装备属性-生命值恢复
     */
    private Long equipHealthRegen;
    /**
     * 装备属性-法力值恢复
     */
    private Long equipManaRegen;
    /**
     * 装备属性-护甲
     */
    private Long equipArmor;
    /**
     * 装备属性-魔抗
     */
    private Long equipMagicResist;
    /**
     * 装备属性-攻击力
     */
    private Long equipAttackDamage;
    /**
     * 装备属性-法攻
     */
    private Long equipAttackSpell;
}
