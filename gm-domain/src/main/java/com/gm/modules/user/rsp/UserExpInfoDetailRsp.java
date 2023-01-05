package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家道具
 */
@Data
public class UserExpInfoDetailRsp {
	/**
	 * NFT_TOKENID
	 */
	private Long nftId;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 经验数量
	 */
	private Integer expNum;
	/**
	 * 经验名称
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
