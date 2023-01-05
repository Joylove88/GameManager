package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 装备基础表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 17:20:39
 */
@Data
@TableName("GM_EQUIPMENT_INFO")
public class EquipmentInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long equipId;
	/**
	 * 装备名称
	 */
	@NotNull(message = "Equipment name cannot be empty!")
	private String equipName;
	/**
	 * 装备稀有度(1:白色,2:绿色,3:蓝色,4:紫色,5:橙色)
	 */
	private String equipRarecode;
	/**
	 * 物品估值
	 */
	private Double itemValuation;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
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
	 * 装备描述
	 */
	private String description;
	/**
	 * 装备合成公式json
	 */
	private String equipJson;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 初始生命值
	 */
	private Long health;
	/**
	 * 初始法力值
	 */
	private Long mana;
	/**
	 * 初始生命值恢复
	 */
	private Double healthRegen;
	/**
	 * 初始法力值恢复
	 */
	private Double manaRegen;
	/**
	 * 初始护甲
	 */
	private Long armor;
	/**
	 * 初始魔抗
	 */
	private Long magicResist;
	/**
	 * 初始攻击力
	 */
	private Long attackDamage;
	/**
	 * 法功
	 */
	private Long attackSpell;
	/**
	 * 创建人
	 */
	private Long createUser;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 修改人
	 */
	private Long updateUser;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

}
