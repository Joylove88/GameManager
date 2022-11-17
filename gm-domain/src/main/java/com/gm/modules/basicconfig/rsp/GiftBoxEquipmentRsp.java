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
public class GiftBoxEquipmentRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 装备\卷轴名称
	 */
	private String equipName;
	/**
	 * 装备\卷轴图标
	 */
	private String equipIconUrl;
	/**
	 * 装备\卷轴数量
	 */
	private Integer equipFragNum;
	/**
	 * 装备类型：0 装备,1 卷轴
	 */
	private Integer equipType;
	/**
	 * 装备\卷轴稀有度
	 */
	private Integer equipRareCode;

}
