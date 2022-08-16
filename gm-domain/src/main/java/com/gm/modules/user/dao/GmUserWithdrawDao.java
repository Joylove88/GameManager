package com.gm.modules.user.dao;

import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-08-09 19:13:43
 */
@Mapper
public interface GmUserWithdrawDao extends BaseMapper<GmUserWithdrawEntity> {
	
}
