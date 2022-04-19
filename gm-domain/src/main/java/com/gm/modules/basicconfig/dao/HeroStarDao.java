package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.HeroStarEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Mapper
public interface HeroStarDao extends BaseMapper<HeroStarEntity> {
    List<HeroStarEntity> getHeroStarPro(Long gmProbabilityId);
}
