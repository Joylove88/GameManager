package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 英雄技能表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Data
@TableName("GM_HERO_SKILL")
public class HeroSkillEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long heroSkillId;
	/**
	 * 英雄ID
	 */
	private Long heroId;
	/**
	 * 技能名称
	 */
	private String skillName;
	/**
	 * 伤害星级
	 */
	private Long skillStarCode;
	/**
	 * 魔法值消耗
	 */
	private Long skillMp;
	/**
	 * 技能类型（0：输出，1：辅助）
	 */
	private String skillType;
	/**
	 * 技能位置
	 */
	private Integer skillSolt;
	/**
	 * 固定伤害
	 */
	private Double skillFixedDamage;
	/**
	 * 英雄属性伤害加成
	 */
	private Double skillDamageBonusHero;
	/**
	 * 装备属性伤害加成
	 */
	private Double skillDamageBonusEquip;
	/**
	 * 技能描述
	 */
	private String skillDescription;
	/**
	 * 状态('0':禁用，'1':启用)
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

	@TableField(exist = false)
	private String heroName;

}
