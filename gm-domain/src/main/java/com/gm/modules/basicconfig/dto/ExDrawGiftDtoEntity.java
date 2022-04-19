package com.gm.modules.basicconfig.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Data
public class ExDrawGiftDtoEntity implements Serializable {
	/**
	 * 经验药水id
	 */
	private Long exId;

	/**
	 * 药水稀有度
	 */
	private String exRare;

	/**
	 * 概率
	 */
	private Double pron;

	/**
	 * 经验药水数量
	 */
	private Long exNum;

	/**
	 * 经验药水名称
	 */
	private String exName;

	/**
	 * 经验图标地址
	 */
	private String exIconUrl;

	/**
	 * 药水描述
	 */
	private String exDescription;

	/**
	 * 经验值
	 */
	private Long exValue;

}
