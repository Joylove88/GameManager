/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.rsp.UserInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 用户
 *
 * @author Axiang Axiang@gmail.com
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    int queryEffectiveUserCount(Long userId);

    /**
     * 获取玩家信息
     * @param map
     * @return
     */
    UserInfoRsp getPlayerInfo(Map<String, Object> map);
}
