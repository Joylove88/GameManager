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
	private Long healthRegen;
	/**
	 * 法力值恢复
	 */
	private Long manaRegen;
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
	 * 法功
	 */
	private Long attackSpell;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
}
