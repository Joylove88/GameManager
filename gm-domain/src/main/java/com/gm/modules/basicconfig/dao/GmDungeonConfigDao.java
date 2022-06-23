package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.GmDungeonConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 副本配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Mapper
public interface GmDungeonConfigDao extends BaseMapper<GmDungeonConfigEntity> {
	
}
