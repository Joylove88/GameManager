package com.gm.modules.basicconfig.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 活动信息
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Data
public class SummonedEventDto implements Serializable {

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 召唤返利开关：0：关闭，1开启
	 */
	private String summonRebateSwitch;

	/**
	 * 已购买数量
	 */
	private Integer quantityUsed;

	/**
	 * 数量限制
	 */
	private Integer quantityAvailable;

	/**
	 * 折扣率
	 */
	private Double discountRate;

	/**
	 * 单抽原价格
	 */
	private Double onePrice;

	/**
	 * 十连抽原价格
	 */
	private Double tenPrice;

	/**
	 * 单抽折扣后的价格
	 */
	private Double onePriceNew;

	/**
	 * 十连抽折扣后的价格
	 */
	private Double tenPriceNew;

	/**
	 * 返单抽金币
	 */
	private Double rebateOne;

	/**
	 * 返十连抽金币
	 */
	private Double rebateTen;

}
