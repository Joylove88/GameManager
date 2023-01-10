package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 队伍配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Data
@TableName("gm_team_config")
public class GmTeamConfigEntity implements Serializable {
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
	 * 战斗记录ID
	 */
	private Long combatId;
	/**
	 * 队伍名称
	 */
	private String teamName;
	/**
	 * 队伍顺序
	 */
	private Integer teamSolt;
	/**
	* 玩家英雄1ID
	*/
	private Long userHero1Id;
	/**
	 * 玩家英雄2ID
	 */
	private Long userHero2Id;
	/**
	 * 玩家英雄3ID
	 */
	private Long userHero3Id;
	/**
	 * 玩家英雄4ID
	 */
	private Long userHero4Id;
	/**
	 * 玩家英雄5ID
	 */
	private Long userHero5Id;
	/**
	 * 队伍战力
	 */
	private Double teamPower;
	/**
	 * 队伍矿工数
	 */
	private BigDecimal teamMinter;
	/**
	 * 战斗状态（0：未战斗，1：战斗中，2：战斗结束）
	 */
	private String status;
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
	 * 战斗开始时间
	 */
	private Date startTime;
	/**
	 * 战斗开始时间
	 */
	private Long startTimeTs;
	/**
	 * 战斗结束时间
	 */
	private Date endTime;
	/**
	 * 战斗结束时间
	 */
	private Long endTimeTs;

	@TableField(exist = false)
	private String userAddress;

}
