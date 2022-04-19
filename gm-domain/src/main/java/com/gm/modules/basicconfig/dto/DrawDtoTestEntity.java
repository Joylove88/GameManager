package com.gm.modules.basicconfig.dto;

import java.io.Serializable;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
public class DrawDtoTestEntity implements Serializable {
	/**
	 * 英雄概率等级
	 */
	private Double gmProbability;

	/**
	 * 英雄名称
	 */
	private String heroName;

	public DrawDtoTestEntity(String name, Double prob) {
		this.heroName = name;
		this.gmProbability = prob;
	}

	public Double getGmProbability() {
		return gmProbability;
	}

	public void setGmProbability(Double gmProbability) {
		this.gmProbability = gmProbability;
	}

	public String getHeroName() {
		return heroName;
	}

	public void setHeroName(String heroName) {
		this.heroName = heroName;
	}
}
