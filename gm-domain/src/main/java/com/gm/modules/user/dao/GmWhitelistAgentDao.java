package com.gm.modules.user.dao;

import com.gm.modules.user.entity.GmWhitelistAgentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代理白名单
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
@Mapper
public interface GmWhitelistAgentDao extends BaseMapper<GmWhitelistAgentEntity> {
	
}
