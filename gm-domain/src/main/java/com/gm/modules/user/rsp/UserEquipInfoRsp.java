package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家装备
 */
@Data
public class UserEquipInfoRsp {
    /**
	 * 玩家装备id
	 */
	private Long userEquipmentId;
    /**
	 * 装备id
	 */
	private Long equipmentId;
	/**
     * 装备名称
	 */
	private String equipName;
	/**
     * 装备稀有度(1:白色,2:绿色,3:蓝色,4:紫色,5:橙色)
	 */
	private String equipRarecode;
	/**
	 * 状态('0':禁用，'1':未激活, '2':已激活)
	 */
	private String status;
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
	 * 装备战力
	 */
	private Long equipPower;
	/**
	 * 生命值
	 */
	private Long equipHealth;
	/**
	 * 法力值
	 */
	private Long equipMana;
	/**
	 * 生命值恢复
	 */
	private Long equipHealthRegen;
	/**
	 * 法力值恢复
	 */
	private Long equipManaRegen;
	/**
	 * 护甲
	 */
	private Long equipArmor;
	/**
	 * 魔抗
	 */
	private Long equipMagicResist;
	/**
	 * 攻击力
	 */
	private Long equipAttackDamage;
	/**
	 * 法功
	 */
	private Long equipAttackSpell;

	/**
	 * 0:装备, 1:装备卷轴\碎片
	 */
	private Long equipType;

	/**
	 * 装备卷轴\碎片数量 默认1
	 */
	private Long equipFragNum;

}
