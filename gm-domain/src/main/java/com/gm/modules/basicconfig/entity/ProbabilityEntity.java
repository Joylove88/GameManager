package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽奖概率配置
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-08 16:26:15
 */
@Data
@TableName("GM_PROBABILITY")
public class ProbabilityEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long probabilityId;
	/**
	 * 概率
	 */
	private Double pr;
	/**
	 * 概率等级
	 */
	private Integer prLv;
	/**
	 * 说明
	 */
	private String explanation;
	/**
	 * 类型:1:英雄，2:装备，3:经验丸
	 */
	private String prType;
	/**
	 * 状态：0:禁用，1:启用
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
