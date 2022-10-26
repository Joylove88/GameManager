package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家经验药水信息表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
@Data
@TableName("GM_USER_EXPERIENCE_POTION")
public class UserExperiencePotionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long userExPotionId;
	/**
	 * 经验药水ID
	 */
	private Long exPotionId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 经验药水数量
	 */
	private Long userExNum;
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
	private String exPotionName;
	@TableField(exist = false)
	private String exPotionRareCode;
	@TableField(exist = false)
	private Long exValue;
	@TableField(exist = false)
	private String userHeroId;

}
