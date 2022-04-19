package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-27 20:23:36
 */
@Data
@TableName("GM_HERO_FRAG")
public class HeroFragEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long gmHeroFragId;
	/**
	 * 英雄ID
	 */
	private Long gmHeroInfoId;
	/**
	 * 英雄碎片数量 默认1
	 */
	private Long gmHeroFragNum;
	/**
	 * 物品估值
	 */
	private Double itemValuation;
	/**
	 * 英雄碎片图标地址
	 */
	private String heroFragIconUrl;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 创建人
	 */
	private String createUser;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 修改人
	 */
	private String updateUser;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

	@TableField(exist = false)
	private String heroName;
	@TableField(exist = false)
	private String heroIconUrl;
}
