package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家装备表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
@Data
@TableName("GM_USER_EQUIPMENT")
public class UserEquipmentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long userEquipmentId;
	/**
	 * 装备ID
	 */
	private Long equipmentId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * NFTID
	 */
	private BigDecimal nftId;
	/**
	 * 矿工数
	 */
	private BigDecimal minter;
	/**
	 * 神谕值
	 */
	private BigDecimal oracle;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 装备战力
	 */
	private Long equipPower;
	/**
	 * 铸造HASH
	 */
	private String mintHash;
	/**
	 * 铸造状态('0':铸造中，'1':铸造成功，'2':铸造失败)
	 */
	private String mintStatus;
	/**
	 * 获取类型（0：副本，1：召唤）
	 */
	private String fromType;
	/**
	 * 来源ID
	 */
	private Long sourceId;
	/**
	 * 状态('0':禁用，'1':未激活, '2':已激活)
	 */
	private String status;
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
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

	@TableField(exist = false)
	private String userName;
	@TableField(exist = false)
	private String equipName;
	@TableField(exist = false)
	private String equipRarecode;
	@TableField(exist = false)
	private String equipLevel;
	@TableField(exist = false)
	private String equipIconUrl;
	@TableField(exist = false)
	private String activationState;

}
