package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户挖矿属性表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-12 19:36:10
 */
@Data
@TableName("gm_mining_info")
public class GmMiningInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 用户矿工数量
	 */
	private BigDecimal miners;
	/**
	 * 用户鸡蛋数量
	 */
	private String claimedEggs;
	/**
	 * 用户上次购买矿工的时间
	 */
	private String lastHatch;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

}
