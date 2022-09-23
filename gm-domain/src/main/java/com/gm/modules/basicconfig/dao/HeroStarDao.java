package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.HeroStarEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Mapper
public interface HeroStarDao extends BaseMapper<HeroStarEntity> {
    /**
     * 通过英雄概率等级获取对应的英雄
     * @param gmProbabilityId
     * @return
     */
    List<HeroStarEntity> getHeroStarPro(Long gmProbabilityId);

    /**
     * 获取全部星级英雄
     * @return
     */
    List<HeroStarEntity> getHeroStars();

    /**
     * 获取指定星级范围英雄
     * @param map
     * @return
     */
    List<HeroStarEntity> getRangeHeroStars(Map<String, Object> map);
}
