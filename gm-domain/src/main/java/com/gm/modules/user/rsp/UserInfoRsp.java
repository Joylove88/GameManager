package com.gm.modules.user.rsp;

import lombok.Data;

@Data
public class UserInfoRsp {
	/**
	 * 用户钱包地址
	 */
	private String userWalletAddress;
	/**
	 * 总战力
	 */
	private Long totalPower;
	/**
	 * 当前体力值
	 */
	private Long ftg;
	/**
	 * 最大体力值
	 */
	private Long ftgMax;
	/**
	 * 总资产
	 */
	private Double totalAmount;
	/**
	 * 玩家级别
	 */
	private Long levelCode;
	/**
	 * 晋级到下一级所需经验值
	 */
	private Long promotionExperience;
	/**
	 * 当前等级获取的经验值
	 */
	private Long currentExp;

}
