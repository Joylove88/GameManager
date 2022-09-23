package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:10:34
 */
@Data
@TableName("GM_USER_HERO")
public class UserHeroEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long gmUserHeroId;
	/**
	 * 用户ID
	 */
	private Long gmUserId;
	/**
	 * NFTID
	 */
	private BigDecimal nftId;
	/**
	 * 用户获得的英雄ID
	 */
	private Long gmHeroStarId;
	/**
	 * 用户获得的英雄ID
	 */
	private Long gmHeroId;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 英雄战力
	 */
	private Long heroPower;
	/**
	 * 累计获得的经验
	 */
	private Long experienceObtain;
	/**
	 * 铸造HASH
	 */
	private String mintHash;
	/**
	 * 铸造状态('0':铸造中，'1':铸造成功，'2':铸造失败)
	 */
	private String mintStatus;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 上阵状态('0':未上阵，'1':上阵中)
	 */
	private String statePlay;
	/**
	 * 英雄等级ID
	 */
	private Long gmHeroLevelId;
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
	private String heroName;
	@TableField(exist = false)
	private String userName;
	@TableField(exist = false)
	private String gmLevelName;
	/**
	 * 英雄星级
	 */
	@TableField(exist = false)
	private Long gmStarCode;
	/**
	 * 英雄等级编码
	 */
	@TableField(exist = false)
	private String gmLevelCode;
	/**
	 * 英雄图片地址
	 */
	@TableField(exist = false)
	private String heroImgUrl;
	/**
	 * 英雄图标地址
	 */
	@TableField(exist = false)
	private String heroIconUrl;
	/**
	 * 英雄龙骨地址
	 */
	@TableField(exist = false)
	private String heroKeelUrl;
	/**
	 * 英雄描述
	 */
	@TableField(exist = false)
	private String heroDescription;

}
