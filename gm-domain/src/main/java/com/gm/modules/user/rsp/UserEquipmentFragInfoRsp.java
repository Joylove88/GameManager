package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家装备碎片
 */
@Data
public class UserEquipmentFragInfoRsp {
	/**
	 * ID
	 */
	private Long equipId;
	/**
	 * NFT_TOKENID
	 */
	private Long nftId;
	/**
     * 装备名称
	 */
	private String equipName;
	/**
     * 装备稀有度
	 */
	private String equipRarecode;
	/**
	 * 装备描述
	 */
	private String description;
	/**
     * 装备碎片数量
	 */
	private Long equipFragNum;
	/**
     * 装备碎片图标地址
	 */
	private String equipFragIconUrl;

}
