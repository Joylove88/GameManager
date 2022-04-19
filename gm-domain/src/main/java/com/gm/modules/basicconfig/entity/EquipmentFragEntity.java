package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 装备碎片表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 19:09:21
 */
@Data
@TableName("GM_EQUINPMENT_FRAG")
public class EquipmentFragEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long equipmentFragId;
	/**
	 * 装备ID
	 */
	private Long equipmentId;
	/**
	 * 装备碎片数量 默认1
	 */
	private Long equipmentFragNum;
	/**
	 * 物品估值
	 */
	private Double itemValuation;
	/**
	 * 装备碎片图标地址
	 */
	private String equipFragIconUrl;
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

}
