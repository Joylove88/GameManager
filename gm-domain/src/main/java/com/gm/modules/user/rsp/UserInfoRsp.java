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
	 * 体力值
	 */
	private Long ftg;
	/**
	 * 用户级别
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
