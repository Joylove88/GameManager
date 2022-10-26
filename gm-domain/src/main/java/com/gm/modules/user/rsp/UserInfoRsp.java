package com.gm.modules.user.rsp;

import lombok.Data;

@Data
public class UserInfoRsp {
	/**
	 * 用户钱包地址
	 */
	private String address;
	/**
	 * 总战力
	 */
	private Long power;
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
	private Double mrCoins;
	/**
	 * 玩家级别
	 */
	private Long levelCode;
	/**
	 * VIP等级
	 */
	private Long vipLevelCode;
	/**
	 * VIP名称
	 */
	private String vipLevelName;
	/**
	 * VIP图标
	 */
	private String vipImgUrl;
	/**
	 * 晋级到下一级所需经验值
	 */
	private Long nextLevelExp;
	/**
	 * 当前等级获取的经验值
	 */
	private Long currentExp;
	/**
	 * 头像url
	 */
	private String imgUrl;
	/**
	 * 英雄数量
	 */
	private Integer heroCount;
	/**
	 * 黄金英雄数量
	 */
	private Integer goldHeroCount;
	/**
	 * 装备数量
	 */
	private Integer equipmentCount;
	/**
	 * 晋级到下一级所需经验值
	 */
	private Long promotionExperience;
	/**
	 * 升级所需累计经验
	 */
	private Long experienceTotal;

}
