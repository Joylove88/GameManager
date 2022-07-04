package com.gm.modules.user.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoRsp {
	/**
	 * ID
	 */
	private Long gmUserHeroId;
	/**
	 * 英雄名称
	 */
	private String heroName;
	/**
	 * 英雄战力
	 */
	private Long heroPower;
	/**
	 * 英雄星级
	 */
	private String gmStarCode;
	/**
	 * 英雄等级编码
	 */
	private String gmLevelCode;
	/**
	 * 英雄图片地址
	 */
	private String heroImgUrl;
	/**
	 * 英雄图标地址
	 */
	private String heroIconUrl;
	/**
	 * 英雄龙骨地址
	 */
	private String heroKeelUrl;
	/**
	 * 累计获得的经验
	 */
	private Long experienceObtain;
	/**
	 * 英雄描述
	 */
	private String heroDescription;

	private List<UserHeroEquipmentWearRsp> wearEQList = new ArrayList<>();


}
