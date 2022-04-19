package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家英雄碎片
 */
@Data
public class UserHeroFragInfoRsp {
	/**
	 * 英雄名称
	 */
	private String heroName;
	/**
	 * 英雄碎片图标地址
	 */
	private String heroFragIconUrl;
	/**
	 * 英雄碎片数量
	 */
	private Long heroFragNum;

}
