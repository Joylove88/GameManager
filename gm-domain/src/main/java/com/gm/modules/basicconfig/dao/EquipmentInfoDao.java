package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 装备基础表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 17:20:39
 */
@Mapper
public interface EquipmentInfoDao extends BaseMapper<EquipmentInfoEntity> {
    List<EquipmentInfoEntity> queryList(Map<String, Object> map);
}
