package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖盒子中的经验信息
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Data
public class GiftBoxExpRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 经验名称
	 */
	private String expName;
	/**
	 * 经验图标
	 */
	private String expIconUrl;
	/**
	 * 经验值
	 */
	private Long expValue;
	/**
	 * 经验描述
	 */
	private String expDescription;
	/**
	 * 经验数量
	 */
	private Integer expNum;
	/**
	 * 经验稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private Integer expRare;

}
