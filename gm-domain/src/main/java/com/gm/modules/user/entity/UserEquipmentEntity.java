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
	 * NFT_TOKENID
	 */
	private Long nftId;
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
	private Double equipPower;
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
