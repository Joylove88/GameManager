package com.gm.modules.user.dao;

import com.gm.modules.user.entity.UserAccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户资金账户
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@Mapper
public interface UserAccountDao extends BaseMapper<UserAccountEntity> {
	
}
