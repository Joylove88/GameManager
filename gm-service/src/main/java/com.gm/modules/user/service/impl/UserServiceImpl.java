/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.UserAccountDao;
import com.gm.modules.user.dao.UserDao;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.entity.UserTokenEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.service.UserTokenService;
import com.gm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
	@Autowired
	private UserTokenService tokenService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserAccountDao userAccountDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<UserEntity> page = this.page(
				new Query<UserEntity>().getPage(params),
				new QueryWrapper<UserEntity>()
		);

		return new PageUtils(page);
	}


	@Override
	public UserEntity queryByMobile(String mobile) {
		return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", mobile));
	}

	@Override
	public UserEntity queryByAddress(String userAddress) {
		return baseMapper.selectOne(new QueryWrapper<UserEntity>()
				.eq("USER_WALLET_ADDRESS",userAddress)
		);
	}

	@Override
	public UserEntity userRegister(UserEntity userEntity) {
		UserEntity userRegister = new UserEntity();
		Date now = new Date();
		userRegister.setSignDate(userEntity.getSignDate());
		userRegister.setCreateTime(now);
		userRegister.setCreateTimeTs(now.getTime());
		userRegister.setUserName(userEntity.getUserWalletAddress());
		userRegister.setUserWalletAddress(userEntity.getUserWalletAddress());
		userRegister.setUserLevel(1L);
		userRegister.setUserType("0");
		userRegister.setStatus(Constant.enable);
		userRegister.setOnlineFlag(Constant.enable);

		userDao.insert(userRegister);

		// 用户注册完成后自动添加一条用户资金账户数据
		UserAccountEntity userAccountEntity = new UserAccountEntity();
		userAccountEntity.setUserId(userRegister.getUserId());
		userAccountEntity.setBalance(0d);
		userAccountEntity.setTotalAmount(0d);
		userAccountEntity.setFrozen(0d);
		userAccountEntity.setStatus(Constant.enable);
		userAccountDao.insert(userAccountEntity);

		return userRegister;
	}

	@Override
	public Map<String, Object> login(UserEntity user) {
		//更改SignDate
		UserEntity ur = new UserEntity();
		ur.setUserId(user.getUserId());
		ur.setSignDate(user.getSignDate());
		userDao.updateById(ur);

		//获取登录token
		UserTokenEntity userTokenEntity = tokenService.createToken(user.getUserId());

		Map<String, Object> map = new HashMap<>(2);
		map.put("token", userTokenEntity.getToken());
		map.put("expire", userTokenEntity.getExpireTime().getTime() - System.currentTimeMillis());

		return map;
	}

}
