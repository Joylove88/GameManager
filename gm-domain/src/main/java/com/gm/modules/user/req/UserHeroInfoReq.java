package com.gm.modules.user.req;

import lombok.Data;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoReq {
	/**
	 * ID
	 */
	private Long gmUserHeroId;
	/**
	 * 玩家装备ID
	 */
	private Long gmUserEquipmentId;
	/**
	 * 父子级装备链
	 */
	private String parentEquipChain;

}
