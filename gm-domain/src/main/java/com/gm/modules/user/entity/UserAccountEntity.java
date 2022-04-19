package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户资金账户
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@Data
@TableName("GM_USER_ACCOUNT")
public class UserAccountEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long accountId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 总资产
	 */
	private Double totalAmount;
	/**
	 * 可用余额
	 */
	private Double balance;
	/**
	 * 冻结金额，一般指提现金额
	 */
	private Double frozen;
	/**
	 * 账户状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 币种
	 */
	private String currency;

}
