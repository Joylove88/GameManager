package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 战斗记录表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-24 15:46:33
 */
@Data
@TableName("gm_combat_record")
public class GmCombatRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 副本ID
	 */
	private Long dungeonId;
	/**
	 * 队伍ID
	 */
	private Long teamId;
	/**
	 * 获得的金币
	 */
	private BigDecimal getGoldCoins;
	/**
	 * 获得的装备
	 */
	private String getEquip;
	/**
	 * 获得的道具
	 */
	private String getProps;
	/**
	 * 战斗描述
	 */
	private String combatDescription;
	/**
	 * 获得的英雄经验
	 */
	private Long getExp;
	/**
	 * 获得的用户经验
	 */
	private Long getUserExp;
	/**
	 * 战斗结果状态（0：YOULOSE，1：YOUWIN，2：战斗中）
	 */
	private String status;
	/**
	 * 战斗开始时间
	 */
	private Date createTime;
	/**
	 * 战斗开始时间
	 */
	private Long createTimeTs;
	/**
	 * 战斗结束时间
	 */
	private Date updateTime;
	/**
	 * 战斗结束时间
	 */
	private Long updateTimeTs;

}
