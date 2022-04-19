package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.modules.basicconfig.entity.HeroFragEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-27 20:23:36
 */
@Mapper
public interface HeroFragDao extends BaseMapper<HeroFragEntity> {
    List<HeroFragEntity> queryList(Page pagination, Map<String, Object> map);
    List<HeroFragEntity> getHeroFragPro();
}
