package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:04:10
 */
@Mapper
public interface HeroInfoDao extends BaseMapper<HeroInfoEntity> {
    List<HeroInfoEntity> queryList(Map<String, Object> map);
}
