package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 经验药水表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
@Data
@TableName("GM_EXPERIENCE_POTION")
public class ExperiencePotionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long gmExPotionId;
	/**
	 * 经验药水名称
	 */
	private String exPotionName;
	/**
	 * 药水稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private String exPotionRareCode;
	/**
	 * 经验值
	 */
	private Long exValue;
	/**
	 * 药水描述
	 */
	private String exDescription;
	/**
	 * 经验图标地址
	 */
	private String exIconUrl;
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

}
