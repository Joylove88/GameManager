package com.gm.modules.user.rsp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
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
	@TableId
	private Long gmUserHeroEquipmentWearId;
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

}
