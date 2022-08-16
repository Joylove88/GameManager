package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.HeroSkillEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 英雄技能表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@Mapper
public interface HeroSkillDao extends BaseMapper<HeroSkillEntity> {

    /**
     * 获取英雄技能信息
     * @param map
     * @return
     */
    HeroSkillEntity getHeroSkill(Map<String, Object> map);
}
