package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Data
@TableName("GM_HERO_STAR")
public class HeroStarEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long gmHeroStarId;
	/**
	 * 英雄ID
	 */
	private Long gmHeroId;
	/**
	 * 星级ID
	 */
	private Long gmStarId;
	/**
	 * 概率等级ID
	 */
	private Long gmProbabilityId;
	/**
	 * 物品估值
	 */
	private Double itemValuation;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 初始生命值
	 */
	private Long gmHealth;
	/**
	 * 成长属性-生命值
	 */
	private Long gmGrowHealth;
	/**
	 * 初始法力值
	 */
	private Long gmMana;
	/**
	 * 成长属性-法力值
	 */
	private Long gmGrowMana;
	/**
	 * 初始生命值恢复
	 */
	private Long gmHealthRegen;
	/**
	 * 成长属性-生命值恢复
	 */
	private Long gmGrowHealthRegen;
	/**
	 * 初始法力值恢复
	 */
	private Long gmManaRegen;
	/**
	 * 成长属性-法力值恢复
	 */
	private Long gmGrowManaRegen;
	/**
	 * 初始护甲
	 */
	private Long gmArmor;
	/**
	 * 成长属性-护甲
	 */
	private Long gmGrowArmor;
	/**
	 * 初始魔抗
	 */
	private Long gmMagicResist;
	/**
	 * 成长属性-魔抗
	 */
	private Long gmGrowMagicResist;
	/**
	 * 初始攻击力
	 */
	private Long gmAttackDamage;
	/**
	 * 成长属性-攻击力
	 */
	private Long gmGrowAttackDamage;
	/**
	 * 初始法攻
	 */
	private Long gmAttackSpell;
	/**
	 * 成长属性-法攻
	 */
	private Long gmGrowAttackSpell;
	/**
	 * 创建人
	 */
	private String createUser;
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
	private String updateUser;
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
	@TableField(exist = false)
	private String heroIconUrl;
	@TableField(exist = false)
	private Long gmStarCode;

}
