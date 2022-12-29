package com.gm.modules.user.rsp;

import com.gm.modules.basicconfig.rsp.HeroSkillRsp;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoDetailRsp {
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
     * 英雄战力
     */
    private Long heroPower;
    /**
     * 英雄星级
     */
    private Integer starCode;
    /**
     * 英雄等级编码
     */
    private Integer levelCode;
    /**
     * 英雄图片地址
     */
    private String heroImgUrl;
    /**
     * 英雄图标地址
     */
    private String heroIconUrl;
    /**
     * 英雄龙骨地址
     */
    private String heroKeelUrl;
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
     * 英雄技能
     */
    private HeroSkillRsp heroSkillRsp = null;


}
