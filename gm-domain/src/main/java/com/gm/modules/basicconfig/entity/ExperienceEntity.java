package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 经验道具表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
@Data
@TableName("GM_EXPERIENCE")
public class ExperienceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 经验名称
	 */
	private String expName;
	/**
	 * 经验道具稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
	 */
	private String rareCode;
	/**
	 * 经验值
	 */
	private Long exp;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 经验道具描述
	 */
	private String description;
	/**
	 * 经验道具图标地址
	 */
	private String iconUrl;
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
