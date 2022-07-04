package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 副本配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Data
@TableName("gm_dungeon_config")
public class GmDungeonConfigEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主ID
	 */
	@TableId
	private Long id;
	/**
	 * 副本名称
	 */
	private String dungeonName;
	/**
	 * 副本描述
	 */
	private String dungeonDescription;
	/**
	 * 怪物数量随机范围
	 */
	private Integer monsterNum;
	/**
	 * 副本最小等级限制
	 */
	private Integer dungeonLevelMin;
	/**
	 * 副本最大等级限制
	 */
	private Integer dungeonLevelMax;
	/**
	 * 副本奖励等级
	 */
	private Long dungeonAward;
	/**
	 * 副本爆率等级
	 */
	private Long burstRate;
	/**
	 * 副本奖励分配百分比
	 */
	private Double rewardDistribution;
	/**
	 * 副本推荐战力
	 */
	private Long dungeonPower;
	/**
	 * 所需体力(默认6)
	 */
	private Long requiresStamina;
	/**
	 * 产出金币范围
	 */
	private Long rangeGoldCoins;
	/**
	 * 产出装备范围
	 */
	private String rangeEquip;
	/**
	 * 产出道具范围
	 */
	private String rangeProps;
	/**
	 * 地图图片URL
	 */
	private String dungeonImgUrl;
	/**
	 * 账户状态('0':禁用，'1':启用)
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

}
