package com.gm.modules.market.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 市场交易订单
 * 
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:36
 */
@Data
@TableName("gm_market_record")
public class GmMarketRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private BigDecimal id;
	/**
	 * 市场中的物品ID
	 */
	private BigDecimal marketOnlineId;
	/**
	 * 购买者用户ID
	 */
	private BigDecimal buyUserId;
	/**
	 * 售卖者用户ID
	 */
	private BigDecimal sellUserId;
	/**
	 * 售卖价格
	 */
	private BigDecimal sellPrice;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;

}
