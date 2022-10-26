package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 英雄装备栏表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-17 18:07:31
 */
@Data
@TableName("GM_HERO_EQUIPMENT")
public class HeroEquipmentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long heroEquipmentId;
	/**
	 * 英雄ID
	 */
	@NotNull(message = "Please choose a hero!")
	private Long heroId;
	/**
	 * 装备ID
	 */
	@NotNull(message = "Please select equipment!")
	private Long equipId;
	/**
	 * 装备位置
	 */
	private int equipSolt;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 创建人
	 */
	private Long createUser;
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
	private Long updateUser;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

	@TableField(exist = false)
	private String equipName;
	@TableField(exist = false)
	private String equipRarecode;
	@TableField(exist = false)
	private String equipLevel;
	@TableField(exist = false)
	private String equipImgUrl;
	@TableField(exist = false)
	private String equipIconUrl;
	@TableField(exist = false)
	private String equipJson;
	@TableField(exist = false)
	private String heroName;

}
