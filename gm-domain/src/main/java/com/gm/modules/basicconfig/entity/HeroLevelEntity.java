package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 英雄等级表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-03 19:44:44
 */
@Data
@TableName("GM_HERO_LEVEL")
public class HeroLevelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long heroLeveId;
	/**
	 * 等级名称
	 */
	private String levelName;
	/**
	 * 等级描述
	 */
	private String levelDesc;
	/**
	 * 等级编码
	 */
	private Integer levelCode;
	/**
	 * 是否默认，1是，0否
	 */
	private String flag;
	/**
	 * 是否删除，1是，0否
	 */
	private String deleted;
	/**
	 * 晋级到下一级所需经验值
	 */
	private Long promotionExperience;
	/**
	 * 升级所需累计经验
	 */
	private Long experienceTotal;
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

}
