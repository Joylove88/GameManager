/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.GmMiningInfoDao;
import com.gm.modules.user.dao.GmWhitelistAgentDao;
import com.gm.modules.user.dao.UserAccountDao;
import com.gm.modules.user.dao.UserDao;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.rsp.UserInfoRsp;
import com.gm.modules.user.service.UserTokenService;
import com.gm.modules.user.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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
	@Autowired
	private GmWhitelistAgentDao whitelistAgentDao;

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
				.eq("ADDRESS",userAddress)
		);
	}

	@Override
	public UserEntity userRegister(UserEntity user) {
		UserEntity userRegister = new UserEntity();
		Date now = new Date();
		userRegister.setSignDate(user.getSignDate());
		userRegister.setCreateTime(now);
		userRegister.setCreateTimeTs(now.getTime());
		userRegister.setUserName(user.getAddress());
		userRegister.setAddress(user.getAddress());
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
		userRegister.setFatherId(user.getFatherId());
		userRegister.setGrandfatherId(user.getGrandfatherId());
		// 获取代理白名单
		GmWhitelistAgentEntity whitelistAgent =whitelistAgentDao.selectOne(new QueryWrapper<GmWhitelistAgentEntity>()
						.eq("STATUS", Constant.enable)
						.eq("ADDRESS", user.getAddress()));
		if (whitelistAgent != null) {
			userRegister.setVipLevelId(1539886242337173506L);
		} else {
			userRegister.setVipLevelId(1539878146445455362L);
		}

		userDao.insert(userRegister);

		// 用户注册完成后自动生成用户战斗账户，代理账户
		int i = 0;
		while (i < 2){
			String currency = i == 0 ? "0" : "1";
			UserAccountEntity userAccountEntity = new UserAccountEntity();
			userAccountEntity.setUserId(userRegister.getUserId());
			userAccountEntity.setBalance(Constant.ZERO_D);
			userAccountEntity.setTotalAmount(Constant.ZERO_D);
			userAccountEntity.setFrozen(Constant.ZERO_D);
			userAccountEntity.setStatus(Constant.enable);
			userAccountEntity.setCurrency(currency);// 0战斗,1代理
			userAccountDao.insert(userAccountEntity);
			i++;
		}

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
	public UserEntity queryByUserId(Long userId) {
		return baseMapper.selectById(userId);
	}

	@Override
	public int queryEffectiveUserCount(UserEntity userEntity) {
		return userDao.queryEffectiveUserCount(userEntity.getUserId());
	}

	@Override
	public List<UserInfoRsp> getPlayerInfo(Map<String, Object> map) {
		return userDao.getPlayerInfo(map);
	}

	@Override
	public Integer queryInviteNum(UserEntity user) {
		return userDao.selectCount(new QueryWrapper<UserEntity>()
				.eq("father_id",user.getUserId())
		);
	}

	@Override
	public UserInfoRsp getTotalPower() {
		return userDao.getTotalPower();
	}

}
