package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖盒子中的经验道具信息
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Data
public class GiftBoxExpRsp implements Serializable {
	/**
	 * 经验道具数量
	 */
	private Integer expNum;
	/**
	 * 经验道具名称
	 */
	private String expName;
	/**
	 * 经验值
	 */
	private Long exp;
	/**
	 * 经验道具稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private String expRare;
	/**
	 * 经验道具图标地址
	 */
	private String iconUrl;
	/**
	 * 经验道具描述
	 */
	private String description;

}
