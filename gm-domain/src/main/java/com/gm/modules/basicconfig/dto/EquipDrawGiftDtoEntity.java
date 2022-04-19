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
public class EquipDrawGiftDtoEntity implements Serializable {
	/**
	 * 装备id
	 */
	private Long equipId;
	/**
	 * 装备卷轴\碎片ID
	 */
	private Long equipmentFragId;
	/**
	 * 装备稀有度
	 */
	private String equipRarecode;
	/**
	 * 装备卷轴\碎片数量 默认1
	 */
	private Long equipmentFragNum;

	private Double pron;

	/**
	 * 0:装备, 1:装备卷轴\碎片
	 */
	private Long dType;

	private String equipName;
	private String equipIconUrl;

	/**
	 * 初始生命值
	 */
	private Double equipHealth;
	/**
	 * 初始法力值
	 */
	private Double equipMana;
	/**
	 * 初始生命值恢复
	 */
	private Double equipHealthRegen;
	/**
	 * 初始法力值恢复
	 */
	private Double equipManaRegen;
	/**
	 * 初始护甲
	 */
	private Double equipArmor;
	/**
	 * 初始魔抗
	 */
	private Double equipMagicResist;
	/**
	 * 初始攻击力
	 */
	private Double equipAttackDamage;
	/**
	 * 法功
	 */
	private Double equipAttackSpell;
}
