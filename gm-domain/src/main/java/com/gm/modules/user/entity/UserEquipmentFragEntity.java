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
	private Long userEquipmentFragId;
	/**
	 * 装备碎片ID
	 */
	private Long equipmentFragId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 玩家获得碎片数量
	 */
	private Integer userEquipFragNum;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 铸造HASH
	 */
	private String mintHash;
	/**
	 * 铸造状态('0':铸造中，'1':铸造成功，'2':铸造失败)
	 */
	private String mintStatus;
	/**
	 * 获取类型（0：副本，1：召唤）
	 */
	private String fromType;
	/**
	 * 来源ID
	 */
	private Long sourceId;
	/**
	 * 状态('0':禁用，'1':未使用, '2':已消耗)
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
