package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 队伍配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Mapper
public interface GmTeamConfigDao extends BaseMapper<GmTeamConfigEntity> {
	
}
