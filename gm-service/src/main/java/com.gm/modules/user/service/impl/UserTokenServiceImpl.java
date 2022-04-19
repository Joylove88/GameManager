/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.modules.user.dao.UserTokenDao;
import com.gm.modules.user.entity.UserTokenEntity;
import com.gm.modules.user.service.UserTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service("tokenService")
public class UserTokenServiceImpl extends ServiceImpl<UserTokenDao, UserTokenEntity> implements UserTokenService {
	/**
	 * 一个月后过期
	 */
	private final static long EXPIRE = 3600 * 24 * 30 ;

	@Override
	public UserTokenEntity queryByToken(String token) {
		return this.getOne(new QueryWrapper<UserTokenEntity>().eq("token", token));
	}

	@Override
	public UserTokenEntity createToken(long userId) {
		//当前时间
		Date now = new Date();
		//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

		//生成token
		String token = generateToken();

		//保存或更新用户token
		UserTokenEntity userTokenEntity = new UserTokenEntity();
		userTokenEntity.setUserId(userId);
		userTokenEntity.setToken(token);
		userTokenEntity.setUpdateTime(now);
		userTokenEntity.setExpireTime(expireTime);
		this.saveOrUpdate(userTokenEntity);

		return userTokenEntity;
	}

	@Override
	public void expireToken(long userId){
		Date now = new Date();

		UserTokenEntity userTokenEntity = new UserTokenEntity();
		userTokenEntity.setUserId(userId);
		userTokenEntity.setUpdateTime(now);
		userTokenEntity.setExpireTime(now);
		this.saveOrUpdate(userTokenEntity);
	}

	private String generateToken(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static void main(String[] args) {
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

		System.out.println(expireTime + "="+EXPIRE * 1000);
	}
}
