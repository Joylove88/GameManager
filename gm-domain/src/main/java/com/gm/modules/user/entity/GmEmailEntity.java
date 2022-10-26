package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gm.common.utils.UniversalStrategyModeTool;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * 玩家邮箱
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-10-20 18:37:41
 */
@Data
@TableName("gm_email")
public class GmEmailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 邮箱地址
	 */
	private String email;
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
