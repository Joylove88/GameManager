package com.gm.modules.user.req;

import lombok.Data;

/**
 * 玩家信息
 */
@Data
public class UserInfoReq {
	/**
	 * 玩家钱包地址
	 */
	private String address;
	/**
	 * 玩家邮箱
	 */
	private String email;
}
