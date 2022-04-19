package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家英雄装备穿戴表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 14:47:27
 */
@Data
@TableName("GM_USER_HERO_EQUIPMENT_WEAR")
public class UserHeroEquipmentWearEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long gmUserHeroEquipmentWearId;
	/**
	 * 英雄ID
	 */
	private Long gmHeroId;
	/**
	 * 装备ID
	 */
	private Long gmEquipId;
	/**
	 * 用户ID
	 */
	private Long gmUserId;
	/**
	 * 父级装备链
	 */
	private String parentEquipChain;
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
	private String heroName;
	@TableField(exist = false)
	private String userName;

}
