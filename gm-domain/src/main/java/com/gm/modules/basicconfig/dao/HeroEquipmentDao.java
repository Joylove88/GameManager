package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.HeroEquipmentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 英雄装备栏表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-17 18:07:31
 */
@Mapper
public interface HeroEquipmentDao extends BaseMapper<HeroEquipmentEntity> {
    /**
     * 获取英雄装备栏
     * @param map
     * @return
     */
    List<HeroEquipmentEntity> getHeroEquipments(Map<String, Object> map);
}
