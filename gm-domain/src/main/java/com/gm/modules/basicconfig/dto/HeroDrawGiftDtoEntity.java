package com.gm.modules.basicconfig.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Data
public class HeroDrawGiftDtoEntity implements Serializable {
	/**
	 * id
	 */
	private Long gmHeroStarId;
	/**
	 * 用户获得的英雄ID
	 */
	private Long gmHeroId;
	/**
	 * 英雄概率等级
	 */
	private Long gmProbability;
	/**
	 * 初始生命值
	 */
	private Double gmHealth;
	/**
	 * 初始法力值
	 */
	private Double gmMana;
	/**
	 * 初始生命值恢复
	 */
	private Double gmHealthRegen;
	/**
	 * 初始法力值恢复
	 */
	private Double gmManaRegen;
	/**
	 * 初始护甲
	 */
	private Double gmArmor;
	/**
	 * 初始魔抗
	 */
	private Double gmMagicResist;
	/**
	 * 初始攻击力
	 */
	private Double gmAttackDamage;
	/**
	 * ID
	 */
	private Long gmHeroFragId;
	/**
	 * 英雄碎片数量 默认1
	 */
	private Long gmHeroFragNum;

	private Double pron;

	/**
	 * 0:英雄, 1:英雄碎片
	 */
	private Long dType;

	private String heroName;
	private String heroIconUrl;
	private Long gmStarCode;

}
