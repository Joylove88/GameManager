package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家装备
 */
@Data
public class UserEquipInfoRsp {
    /**
	 * 装备id
	 */
	private String gmUserEquipmentId;
	/**
     * 装备名称
	 */
	private String equipName;
	/**
     * 装备稀有度(1:白色,2:绿色,3:蓝色,4:紫色,5:橙色)
	 */
	private String equipRarecode;
	/**
     * 装备等级
	 */
	private String equipLevel;
	/**
     * 装备图标地址
	 */
	private String equipIconUrl;
	/**
	 * 装备战力
	 */
	private Long equipPower;

}
