package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 副本事件表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-10 15:57:41
 */
@Data
@TableName("gm_dungeon_event")
public class GmDungeonEventEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 副本ID
	 */
	private Long dungeonId;
	/**
	 * 事件名称
	 */
	private String eventName;
	/**
	 * 事件描述
	 */
	private String eventDescription;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 事件等级
	 */
	private Long eventLevel;
	/**
	 * 事件触发概率
	 */
	private Double eventPron;
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
	 * 更新者
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
