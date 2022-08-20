package com.gm.modules.user.req;

import lombok.Data;

/**
 * 玩家使用经验
 */
@Data
public class UseExpReq {
	/**
	 * 经验药水数量
	 */
	private Long expNum;
	/**
	 * 药水稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private String expRare;
	/**
	 * 玩家英雄ID
	 */
	private String gmUserHeroId;

}
