package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.ProbabilityEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖概率配置
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-08 16:26:15
 */
@Mapper
public interface ProbabilityDao extends BaseMapper<ProbabilityEntity> {
	
}
