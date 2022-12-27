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
	private String levelCode;
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
	 * 晋级到下一级所需经验值
	 */
	private Long promotionExperience;
	/**
	 * 升级所需累计经验
	 */
	private Long experienceTotal;
	/**
	 * 累计获得的经验
	 */
	private Long experienceObtain;
	/**
	 * 当前等级获取的经验值
	 */
	private Long currentExp;
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
