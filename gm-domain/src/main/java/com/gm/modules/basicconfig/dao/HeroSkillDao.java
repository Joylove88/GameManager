package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.HeroSkillEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 英雄技能表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Mapper
public interface HeroSkillDao extends BaseMapper<HeroSkillEntity> {
	
}
