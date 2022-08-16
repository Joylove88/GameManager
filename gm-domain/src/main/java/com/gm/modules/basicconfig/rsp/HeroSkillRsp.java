package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 英雄技能
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Data
public class HeroSkillRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 技能名称
	 */
	private String skillName;
	/**
	 * 伤害星级
	 */
	private Long skillStarLevel;
	/**
	 * 魔法值消耗
	 */
	private Long skillMp;
	/**
	 * 技能类型（0：输出，1：辅助）
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
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
}
