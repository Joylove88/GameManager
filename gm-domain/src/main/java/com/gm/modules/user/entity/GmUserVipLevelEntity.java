package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户消费等级
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-23 15:25:58
 */
@Data
@TableName("gm_user_vip_level")
public class GmUserVipLevelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long vipLevelId;
	/**
	 * 创建人
	 */
	private String createUser;
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
	private String updateUser;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;
	/**
	 * 等级code
	 */
	private Integer vipLevelCode;
	/**
	 * 等级名称
	 */
	private String vipLevelName;
	/**
	 * VIP图标
	 */
	private String vipImgUrl;
	/**
	 * 消费金额
	 */
	private Double consumeMoney;
	/**
	 * 邀请人数
	 */
	private Integer inviteNum;
	/**
	 * 邀请人数消费金额
	 */
	private Double inviteConsumeMoney;
	/**
	 * 提现上限
	 */
	private Double withdrawLimit;
	/**
	 * 提现频率
	 */
	private Integer withdrawFrequency;
	/**
	 * 首次消费返佣金
	 */
	private Double firstBrokerage;
	/**
	 * 消费返佣金
	 */
	private Double brokerage;
	/**
	 * 提现手续费
	 */
	private Double withdrawHandlingFee;
	/**
	 * 提现是否需要审核（0：需要，1：不需要）
	 */
	private Integer needCheck;

}
