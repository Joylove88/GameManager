package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家装备碎片表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
@Data
@TableName("GM_USER_EQUIPMENT_FRAG")
public class UserEquipmentFragEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long gmUserEquipmentFragId;
	/**
	 * 装备碎片ID
	 */
	private Long gmEquipmentFragId;
	/**
	 * 用户ID
	 */
	private Long gmUserId;
	/**
	 * 玩家获得碎片数量
	 */
	private Long gmUserEquipFragNum;
	/**
	 * 铸造HASH
	 */
	private String mintHash;
	/**
	 * 铸造状态('0':铸造中，'1':铸造成功，'2':铸造失败)
	 */
	private String mintStatus;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

	@TableField(exist = false)
	private String userName;
	@TableField(exist = false)
	private String equipName;
	@TableField(exist = false)
	private String equipRarecode;
	@TableField(exist = false)
	private String equipFragIconUrl;

}
