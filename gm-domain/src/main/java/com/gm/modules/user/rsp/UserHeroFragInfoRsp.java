package com.gm.modules.user.rsp;

import lombok.Data;
import sun.dc.pr.PRError;

/**
 * 玩家英雄碎片
 */
@Data
public class UserHeroFragInfoRsp {
	/**
	 * 英雄ID
	 */
	private String heroInfoId;
	/**
	 * NFT_TOKENID
	 */
	private Long nftId;
	/**
	 * 英雄名称
	 */
	private String heroName;
	/**
	 * 英雄碎片图标地址
	 */
	private String heroFragIconUrl;
	/**
	 * 玩家英雄碎片数量
	 */
	private Integer heroFragNum;
	/**
	 * 玩家碎片矿工比例（这里取的平均值）
	 */
	private Double scale;

}
