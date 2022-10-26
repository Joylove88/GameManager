package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 装备合成公式表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-14 14:07:17
 */
@Data
@TableName("GM_EQUIP_SYNTHESIS_ITEM")
public class EquipSynthesisItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long equipSynthesisItemId;
	/**
	 * 装备ID
	 */
	private Long equipmentId;
	/**
	 * 装备碎片ID
	 */
	private Long equipmentFragId;
	/**
	 * 需要的碎片数量
	 */
	private Long equipFragNum;
	/**
	 * 装备合成项1
	 */
	private String equipSynthesisItem1;
	/**
	 * 装备合成项2
	 */
	private String equipSynthesisItem2;
	/**
	 * 装备合成项3
	 */
	private String equipSynthesisItem3;
	/**
	 * 白装
	 */
	private String equipWhite;
	/**
	 * 蓝装
	 */
	private String equipBlue;
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
	private String equipRare;
	@TableField(exist = false)
	private String equipFragName;
	@TableField(exist = false)
	private String equipItemName1;

}
