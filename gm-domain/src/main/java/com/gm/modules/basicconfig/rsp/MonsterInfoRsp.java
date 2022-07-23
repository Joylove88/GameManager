package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 怪物信息表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Data
public class MonsterInfoRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主ID
	 */
	private Long id;
	/**
	 * 怪物名称
	 */
	private String monsterName;
	/**
	 * 怪物描述
	 */
	private String monsterDescription;
	/**
	 * 怪物等级
	 */
	private Long monsterLevel;
	/**
	 * 怪物战力
	 */
	private Long monsterPower;
	/**
	 * 怪物图片URL
	 */
	private String monsterImgUrl;
	/**
	 * 怪物图标URL
	 */
	private String monsterIconUrl;

}
