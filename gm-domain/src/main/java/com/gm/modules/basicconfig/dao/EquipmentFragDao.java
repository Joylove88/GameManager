package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.EquipmentFragEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 装备碎片表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 19:09:21
 */
@Mapper
public interface EquipmentFragDao extends BaseMapper<EquipmentFragEntity> {
	List<EquipmentFragEntity> getEquipFragInfo();
}
