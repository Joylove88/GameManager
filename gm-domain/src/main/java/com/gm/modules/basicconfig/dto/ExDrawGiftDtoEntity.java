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
	 * 经验道具id
	 */
	private Long exId;

	/**
	 * 经验道具稀有度
	 */
	private String exRare;

	/**
	 * 概率
	 */
	private Double pron;

	/**
	 * 经验道具数量
	 */
	private Long exNum;

	/**
	 * 经验道具名称
	 */
	private String exName;

	/**
	 *经验道具图标地址
	 */
	private String exIconUrl;

	/**
	 * 经验道具描述
	 */
	private String exDescription;

	/**
	 * 经验值
	 */
	private Long exValue;

}
