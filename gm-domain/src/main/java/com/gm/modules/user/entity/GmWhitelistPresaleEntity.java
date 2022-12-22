package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 预售白名单
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
@Data
@TableName("gm_whitelist_presale")
public class GmWhitelistPresaleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 活动类型：0预售
	 */
	private String type;
	/**
	 * 折扣率
	 */
	private Double discountRate;
	/**
	 * 已购买数量
	 */
	private Integer quantityUsed;
	/**
	 * 可购买数量
	 */
	private Integer quantityAvailable;
	/**
	 * 状态：0:禁用，1:启用
	 */
	private String status;
	/**
	 * 活动开始时间
	 */
	private Date startTime;
	/**
	 * 活动结束时间
	 */
	private Date endTime;
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
