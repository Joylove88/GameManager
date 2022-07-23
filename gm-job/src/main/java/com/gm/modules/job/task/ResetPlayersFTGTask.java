/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.job.task;

import com.gm.modules.user.dao.UserDao;
import com.gm.modules.user.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每天早上8点重置全部玩家体力值
 * @author axiang
 */
@Component("resetPlayersFTGTask")
public class ResetPlayersFTGTask implements ITask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserDao userDao;
	
	@Override
	public void run(String params){
//		logger.info("resetPlayersFTGTask定时任务正在执行，参数为：{}", params);
//		try {
//			// 开始重置全部玩家体力值
//			Map<String,Object> map = new HashMap<>();
//			List<UserEntity> users = userDao.selectByMap(map);
//			for (UserEntity userEntity : users){
//				UserEntity user = new UserEntity();
//				user.setUserId(userEntity.getUserId());
//				user.setFtg(60L);
//				userDao.updateById(user);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
