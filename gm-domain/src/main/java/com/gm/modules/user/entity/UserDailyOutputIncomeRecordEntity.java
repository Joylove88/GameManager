package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家每日可产出收益记录表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-04 16:38:24
 */
@Data
@TableName("gm_user_daily_output_income_record")
public class UserDailyOutputIncomeRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private BigDecimal id;
	/**
	 * 用户ID
	 */
	private BigDecimal userId;
	/**
	 * 队伍ID
	 */
	private BigDecimal teamId;
	/**
	 * 第一次时间
	 */
	private Date firstTime;
	/**
	 * 当日可产出最大金币数
	 */
	private BigDecimal maxGoldCoins;
	/**
	 * 当日剩余奖励金币数
	 */
	private BigDecimal remainingGoldCoins;
	/**
	 * 当日可以战斗的最大场数
	 */
	private BigDecimal maxFight;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
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

}
