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
public class GiftBoxPrDto implements Serializable {
	/**
	 * 概率
	 */
	private Double pr;
	/**
	 * 概率等级
	 */
	private Integer prLv;
}
