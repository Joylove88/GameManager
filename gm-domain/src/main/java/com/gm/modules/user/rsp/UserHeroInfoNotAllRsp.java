package com.gm.modules.user.rsp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoNotAllRsp {
	/**
	 * ID
	 */
	private Long userHeroId;
	/**
	 * 英雄名称
	 */
	private String heroName;
	/**
	 * 英雄战力
	 */
	private Long heroPower;
	/**
	 * 英雄矿工数
	 */
	private BigDecimal minter;
	/**
	 * 成长率
	 */
	private Double growthRate;
	/**
	 * 英雄星级
	 */
	private String starCode;
	/**
	 * 英雄等级编码
	 */
	private String gmLevelCode;
	/**
	 * 英雄图片地址
	 */
	private String heroImgUrl;
	/**
	 * 英雄图标地址
	 */
	private String heroIconUrl;
	/**
	 * 英雄龙骨地址
	 */
	private String heroKeelUrl;
}
