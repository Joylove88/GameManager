/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.rsp.UserInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface UserService extends IService<UserEntity> {

	PageUtils queryPage(Map<String, Object> params);


	UserEntity queryByMobile(String mobile);

	UserEntity queryByAddress(String userAddress);

	/**
	 * 用户注册
	 * @param userEntity
	 * @return
	 */
	UserEntity userRegister(UserEntity userEntity);

	/**
	 * 用户登录
	 * @return        返回登录信息
	 */
	Map<String, Object> login(UserEntity userEntity);

	UserEntity queryByExpandCode(String expandCode);

	/**
	 * 根据ID查询用户
	 * @param userId 用户ID
	 * @return 用户
	 */
	UserEntity queryByUserId(Long userId);

	int queryEffectiveUserCount(UserEntity userEntity);

	/**
	 * 获取玩家信息
	 * @param map
	 * @return
	 */
	List<UserInfoRsp> getPlayerInfo(Map<String, Object> map);
}
