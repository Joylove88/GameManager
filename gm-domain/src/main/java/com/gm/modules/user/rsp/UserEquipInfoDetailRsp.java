package com.gm.modules.user.rsp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 玩家装备
 */
@Data
public class UserEquipInfoDetailRsp {
    /**
	 * 玩家装备id
	 */
	private Long userEquipmentId;
    /**
	 * 装备id
	 */
	private Long equipmentId;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 矿工数
	 */
	private BigDecimal minter;
	/**
	 * 神谕值
	 */
	private BigDecimal oracle;
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
	private Integer equipLevel;
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
	private Double equipPower;
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
