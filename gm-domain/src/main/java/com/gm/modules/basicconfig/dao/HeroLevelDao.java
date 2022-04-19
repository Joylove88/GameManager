package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 英雄等级表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-03 19:44:44
 */
@Mapper
public interface HeroLevelDao extends BaseMapper<HeroLevelEntity> {
	List<HeroLevelEntity> getHeroLevel(HeroLevelEntity heroLeveEntity);
}
