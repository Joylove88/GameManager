package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户资金明细：记录余额变动
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@Data
@TableName("GM_USER_BALANCE_DETAIL")
public class UserBalanceDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long userBalanceDetailId;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 交易类型：00提现,01副本产出,02英雄召唤,03装备召唤,04经验召唤,11返点，12提现手续费，15后台取款,16提现冻结,17提现解冻,18后台取款冻结,19,后台取款解冻
	 */
	private String tradeType;
	/**
	 * 交易金额
	 */
	private BigDecimal amount;
	/**
	 * 交易时间
	 */
	private Date tradeTime;
	/**
	 * 交易时间
	 */
	private Long tradeTimeTs;
	/**
	 * 交易描述
	 */
	private String tradeDesc;
	/**
	 * 交易后的余额
	 */
	private Double userBalance;
	/**
	 * 交易类型id：提现id,副本产出id,召唤订单id,市场交易ID,代理返佣id
	 */
	private Long sourceId;

}
