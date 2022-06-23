package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 怪物配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Data
@TableName("gm_monster_config")
public class GmMonsterConfigEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主ID
	 */
	@TableId
	private Long id;
	/**
	 * 副本ID
	 */
	private Long dungeonId;
	/**
	 * 怪物名称
	 */
	private String monsterName;
	/**
	 * 怪物描述
	 */
	private String monsterDescription;
	/**
	 * 怪物等级
	 */
	private Long monsterLevel;
	/**
	 * 怪物战力
	 */
	private Long monsterPower;
	/**
	 * 怪物图片URL
	 */
	private String monsterImgUrl;
	/**
	 * 怪物图标URL
	 */
	private String monsterIconUrl;
	/**
	 * 初始生命值
	 */
	private Long monsterHealth;
	/**
	 * 初始生命值恢复
	 */
	private Long monsterHealthRegen;
	/**
	 * 初始护甲
	 */
	private Long monsterArmor;
	/**
	 * 初始魔抗
	 */
	private Long monsterMagicResist;
	/**
	 * 初始攻击力
	 */
	private Long monsterAttackDamage;
	/**
	 * 法功
	 */
	private Long monsterAttackSpell;
	/**
	 * 击杀经验
	 */
	private Long monsterExp;
	/**
	 * 怪物专属技能伤害倍数
	 */
	private Integer uniqueSkillM;
	/**
	 * 怪物致命一击伤害倍数
	 */
	private Integer criticalHitM;
	/**
	 * 账户状态('0':禁用，'1':启用)
	 */
	private String status;
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
	/**
	 * 副本最小等级限制
	 */
	@TableField(exist = false)
	private Integer dungeonLevelMin;
	/**
	 * 副本最大等级限制
	 */
	@TableField(exist = false)
	private Integer dungeonLevelMax;
	/**
	 * 副本名称
	 */
	@TableField(exist = false)
	private String dungeonName;

}
