package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:04:10
 */
@Data
@TableName("GM_HERO_INFO")
public class HeroInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主ID
	 */
	@TableId
	private Long heroId;
	/**
	 * 英雄名称
	 */
	@NotNull(message = "Hero name cannot be empty!")
	private String heroName;
	/**
	 * 英雄类型
	 */
	@NotNull(message = "Hero type cannot be empty!")
	private String heroType;
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

}
