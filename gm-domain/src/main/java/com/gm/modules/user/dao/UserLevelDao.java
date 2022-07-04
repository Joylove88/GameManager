package com.gm.modules.user.dao;

import com.gm.modules.user.entity.UserLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 玩家等级表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-02 15:28:42
 */
@Mapper
public interface UserLevelDao extends BaseMapper<UserLevelEntity> {
	
}
