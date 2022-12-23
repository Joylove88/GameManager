package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖盒子中的英雄信息
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Data
public class GiftBoxHeroRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 英雄名称
	 */
	private String heroName;
	/**
	 * 英雄图标
	 */
	private String heroIconUrl;
	/**
	 * 英雄星级。如果是英雄碎片默认0
	 */
	private Integer starCode;
	/**
	 * 英雄碎片数量，如果为星级英雄 数量固定1
	 */
	private Integer heroFragNum;
	/**
	 * 英雄类型：0 为星级英雄,  1 为英雄碎片
	 */
	private Integer heroType;
	/**
	 * 皮肤类型：0 原始皮肤,  1 黄金
	 */
	private Integer skinType;
	/**
	 * 抽奖数量
	 */
	private Integer boxNum;

}
