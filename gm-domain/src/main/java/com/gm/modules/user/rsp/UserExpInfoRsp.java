package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家道具
 */
@Data
public class UserExpInfoRsp {
	/**
	 * 经验数量
	 */
	private Long expNum;
	/**
	 * 经验名称
	 */
	private String expName;
	/**
	 * 经验值
	 */
	private Long expValue;
	/**
	 * 药水稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private String expRare;
	/**
	 * 经验图标地址
	 */
	private String expIconUrl;
	/**
	 * 经验描述
	 */
	private String expDescription;

}
