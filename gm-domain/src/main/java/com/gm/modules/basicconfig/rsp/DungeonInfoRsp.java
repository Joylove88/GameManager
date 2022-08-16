package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 副本信息
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Data
public class DungeonInfoRsp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主ID
	 */
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
	 * 副本最小等级限制
	 */
	private Integer dungeonLevelMin;
	/**
	 * 副本最大等级限制
	 */
	private Integer dungeonLevelMax;
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
	 * 怪物集合
	 */
	private List<MonsterInfoRsp> monsterInfoRsps = new ArrayList<>();

	/**
	 * 战斗中的队伍
	 */
	private TeamInfoInBattleRsp teamInfoInBattleRsps = null;

	/**
	 * 装备集合
	 */
	private List<EquipmentInfoRsp> equipmentInfoRsps = new ArrayList<>();

}
