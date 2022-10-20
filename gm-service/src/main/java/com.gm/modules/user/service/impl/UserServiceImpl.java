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
import com.gm.modules.user.dao.GmMiningInfoDao;
import com.gm.modules.user.dao.UserAccountDao;
import com.gm.modules.user.dao.UserDao;
import com.gm.modules.user.entity.GmMiningInfoEntity;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.entity.UserTokenEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.rsp.UserInfoRsp;
import com.gm.modules.user.service.UserTokenService;
import com.gm.modules.user.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
	@Autowired
	private GmMiningInfoDao miningInfoDao;

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
		userRegister.setUserLevelId(1L);
		userRegister.setFtg(Constant.FTG);
		userRegister.setScale(1D);
		userRegister.setTotalPower(Constant.ZERO);
		userRegister.setTotalMinter(BigDecimal.ZERO);
		userRegister.setExperienceObtain(Constant.ZERO);
		userRegister.setUserType(Constant.ZERO_);
		userRegister.setStatus(Constant.enable);
		userRegister.setOnlineFlag(Constant.enable);

		userRegister.setExpandCode(RandomStringUtils.randomAlphanumeric(6));
		userRegister.setFatherId(userEntity.getFatherId());
		userRegister.setGrandfatherId(userEntity.getGrandfatherId());
		userRegister.setVipLevelId(1539878146445455362L);

		userDao.insert(userRegister);

		// 用户注册完成后自动添加一条用户资金账户数据
		UserAccountEntity userAccountEntity = new UserAccountEntity();
		userAccountEntity.setUserId(userRegister.getUserId());
		userAccountEntity.setBalance(Constant.ZERO_D);
		userAccountEntity.setTotalAmount(Constant.ZERO_D);
		userAccountEntity.setFrozen(Constant.ZERO_D);
		userAccountEntity.setStatus(Constant.enable);
		userAccountDao.insert(userAccountEntity);

		// 用户注册完成后自动添加一条矿工数据
		GmMiningInfoEntity miningInfoEntity = new GmMiningInfoEntity();
		miningInfoEntity.setUserId(userRegister.getUserId());
		miningInfoEntity.setMiners(BigDecimal.ZERO);
		miningInfoEntity.setClaimedEggs(Constant.ZERO_);
		miningInfoEntity.setLastHatch(Constant.ZERO_);
		miningInfoEntity.setStatus(Constant.enable);
		miningInfoDao.insert(miningInfoEntity);
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

	@Override
	public UserEntity queryByExpandCode(String expandCode) {
		return baseMapper.selectOne(new QueryWrapper<UserEntity>()
				.eq("expand_code",expandCode)
		);
	}

	@Override
	public UserEntity queryByUserId(Long gmUserId) {
		return baseMapper.selectById(gmUserId);
	}

	@Override
	public int queryEffectiveUserCount(UserEntity userEntity) {
		return userDao.queryEffectiveUserCount(userEntity.getUserId());
	}

	@Override
	public UserInfoRsp getPlayerInfo(Map<String, Object> map) {
		return userDao.getPlayerInfo(map);
	}

}
