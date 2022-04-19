package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录日志
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-08 16:00:57
 */
@Data
@TableName("GM_USER_LOGIN_LOG")
public class UserLoginLogEntity implements Serializable {
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
	 * 用户名
	 */
	private String userCode;
	/**
	 * 登录时间
	 */
	private Date loginTime;
	/**
	 * 登录时间
	 */
	private Long loginTimeTs;
	/**
	 * 登录地点
	 */
	private String loginAddress;
	/**
	 * 登录ip
	 */
	private String loginIp;
	/**
	 * 登录类型
	 */
	private String loginType;
	/**
	 * 状态，00登录，01登出，02请求IP变动记录
	 */
	private String status;
	/**
	 * 响应状态码
	 */
	private String rspCode;
	/**
	 * 
	 */
	private String rspMsg;

	@TableField(exist = false)
	private Long gtTime;
	@TableField(exist = false)
	private Long ltTime;
}
