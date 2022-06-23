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
	 * 队伍名称
	 */
	private String teamName;
	/**
	 * 队伍顺序
	 */
	private Integer teamSolt;
	/**
	 * 英雄1ID
	 */
	private Long hero1Id;
	/**
	 * 英雄2ID
	 */
	private Long hero2Id;
	/**
	 * 英雄3ID
	 */
	private Long hero3Id;
	/**
	 * 英雄4ID
	 */
	private Long hero4Id;
	/**
	 * 英雄5ID
	 */
	private Long hero5Id;
	/**
	 * 队伍战力
	 */
	private Long teamPower;
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

	@TableField(exist = false)
	private String userAddress;

}
