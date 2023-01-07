package com.gm.modules.user.rsp;

import com.gm.modules.basicconfig.rsp.HeroSkillRsp;
import lombok.Data;

import java.math.BigDecimal;
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
	private Long userHeroId;
	/**
	 * 英雄ID
	 */
	private Long heroId;
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
	private Integer starCode;
	/**
	 * 英雄等级编码
	 */
	private Integer levelCode;
	/**
	 * 成长率
	 */
	private Double growthRate;
	/**
	 * 皮肤类型：0 原始皮肤,  1 黄金
	 */
	private Integer skinType;
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
	 * 英雄描述
	 */
	private String heroDescription;
	/**
	 * 英雄已穿戴装备
	 */
	private List<UserHeroEquipmentWearRsp> wearEQList = new ArrayList<>();
	/**
	 * 英雄技能
	 */
	private HeroSkillRsp heroSkillRsp = null;


}
