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
	 * 装备等级
	 */
	private String equipLevel;

	/**
	 * 初始生命值
	 */
	private Long equipHealth;
	/**
	 * 初始法力值
	 */
	private Long equipMana;
	/**
	 * 初始生命值恢复
	 */
	private Long equipHealthRegen;
	/**
	 * 初始法力值恢复
	 */
	private Long equipManaRegen;
	/**
	 * 初始护甲
	 */
	private Long equipArmor;
	/**
	 * 初始魔抗
	 */
	private Long equipMagicResist;
	/**
	 * 初始攻击力
	 */
	private Long equipAttackDamage;
	/**
	 * 法功
	 */
	private Long equipAttackSpell;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
}
