package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家经验道具信息表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
@Data
@TableName("GM_USER_EXPERIENCE")
public class UserExperienceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 经验道具ID
	 */
	private Long expId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * NFT_TOKENID
	 */
	private Long nftId;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 经验道具数量
	 */
	private Integer expNum;
	/**
	 * 铸造HASH
	 */
	private String mintHash;
	/**
	 * 铸造状态('0':铸造中，'1':铸造成功，'2':铸造失败)
	 */
	private String mintStatus;
	/**
	 * 状态('0':禁用，'1':未使用，'2':已使用)
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
	private String expName;
	@TableField(exist = false)
	private String rareCode;
	@TableField(exist = false)
	private Long exp;
	@TableField(exist = false)
	private String userHeroId;

}
