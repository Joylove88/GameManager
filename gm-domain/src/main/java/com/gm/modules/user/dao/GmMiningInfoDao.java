package com.gm.modules.user.dao;

import com.gm.modules.user.entity.GmMiningInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户挖矿属性表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-12 19:36:10
 */
@Mapper
public interface GmMiningInfoDao extends BaseMapper<GmMiningInfoEntity> {
	
}
