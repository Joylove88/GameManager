package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import com.gm.modules.user.rsp.UserHeroEquipmentWearRsp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 玩家英雄装备穿戴表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 14:47:27
 */
@Mapper
public interface UserHeroEquipmentWearDao extends BaseMapper<UserHeroEquipmentWearEntity> {
    /**
     * 获取玩家英雄穿戴中的装备
     * @param map
     * @return
     */
	List<UserHeroEquipmentWearRsp> getUserWearEQ(Map<String, Object> map);
}
