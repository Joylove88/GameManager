package com.gm.modules.user.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩家英雄装备穿戴表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 14:47:27
 */
@Data
public class UserHeroEquipmentWearRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private Long userHeroEquipmentWearId;
	/**
	 * 玩家英雄ID
	 */
	private Long userHeroId;
	/**
	 * 玩家装备ID
	 */
	private Long userEquipId;
	/**
	 * 装备ID
	 */
	private Long equipmentId;
	/**
	 * 父级装备链
	 */
	private String parentEquipChain;
	/**
	 * 状态('0':未激活，'1':已激活)
	 */
	private String status;
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
	 * 生命值
	 */
	private Double health;
	/**
	 * 法力值
	 */
	private Double mana;
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
	private Double armor;
	/**
	 * 魔抗
	 */
	private Double magicResist;
	/**
	 * 攻击力
	 */
	private Double attackDamage;
	/**
	 * 法功
	 */
	private Double attackSpell;

}
