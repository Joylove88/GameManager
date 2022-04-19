package com.gm.modules.user.dao;

import com.gm.modules.user.entity.UserLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户登录日志
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-08 16:00:57
 */
@Mapper
public interface UserLoginLogDao extends BaseMapper<UserLoginLogEntity> {
	List<UserLoginLogEntity> getLogin12H(UserLoginLogEntity userLoginLogEntity);
}
