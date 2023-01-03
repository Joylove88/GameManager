package com.gm.modules.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * 抽奖订单
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-12 19:02:34
 */
@Data
@TableName("GM_TRANSACTION_ORDER")
public class TransactionOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 召唤类型('1':英雄，'2':装备，'3':经验道具)
	 */
	private String summonType;
	/**
	 * 货币类型('0':金币，'1':加密货币)
	 */
	private String currencyType;
	/**
	 * 召唤次数
	 */
	private Integer summonNum;
	/**
	 * 链上交易HASH
	 */
	private String hash;
	/**
	 * 抽到的物品信息
	 */
	private String itemData;
	/**
	 * 状态('0':待处理，'1':成功，'2':失败，‘3’:异常)
	 */
	private String status;
	/**
	 * 区块号
	 */
	private Long blockNumber;
	/**
	 * 订单金额
	 */
	private BigDecimal orderFee;
	/**
	 * 实际支付金额
	 */
	private BigDecimal realFee;
	/**
	 * GAS费
	 */
	private BigDecimal gasFee;
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
