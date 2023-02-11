package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.HeroFragEntity;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import com.gm.modules.basicconfig.rsp.HeroInfoDetailRsp;
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

    /**
     * 获取英雄信息
     * @return
     */
    List<HeroInfoEntity> getHeroInfoPro();

    /**
     * 获取英雄信息集合
     * @return
     */
    List<HeroInfoDetailRsp> getHeroInfoList();


}
