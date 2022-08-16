package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 英雄装备基础表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 17:20:39
 */
@Data
public class HeroEquipmentInfoRsp implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private Long equipId;
	/**
	 * 装备名称
	 */
	private String equipName;
	/**
	 * 装备稀有度(1:白色,2:绿色,3:蓝色,4:紫色,5:橙色)
	 */
	private String equipRarecode;
	/**
	 * 装备等级
	 */
	private Long equipLevel;
	/**
	 * 装备图片地址
	 */
	private String equipImgUrl;
	/**
	 * 装备图标地址
	 */
	private String equipIconUrl;
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
	 * 激活状态(0:未激活，1:已激活)
	 */
	private String status;

}
