package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家道具
 */
@Data
public class UserExpInfoRsp {
	/**
	 * 经验药水数量
	 */
	private Long userExNum;
	/**
	 * 状态('0':禁用，'1':未使用，'2':已使用)
	 */
	private String status;
	/**
	 * 经验药水名称
	 */
	private String exPotionName;
	/**
	 * 经验值
	 */
	private Long exValue;
	/**
	 * 药水稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private String exPotionRareCode;
	/**
	 * 经验图标地址
	 */
	private String exIconUrl;
	/**
	 * 药水描述
	 */
	private String exDescription;

}
