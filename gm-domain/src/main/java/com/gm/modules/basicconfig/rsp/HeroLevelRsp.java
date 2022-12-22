package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 英雄等级表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-03 19:44:44
 */
@Data
public class HeroLevelRsp implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 等级编码
	 */
	private Integer levelCode;
	/**
	 * 晋级到下一级所需经验值
	 */
	private Long promotionExperience;
	/**
	 * 升级所需累计经验
	 */
	private Long experienceTotal;
}
