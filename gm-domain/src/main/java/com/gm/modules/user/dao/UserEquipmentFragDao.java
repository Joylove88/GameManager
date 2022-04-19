package com.gm.modules.user.dao;

import com.gm.modules.user.entity.UserEquipmentFragEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.rsp.UserEquipmentFragInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 玩家装备碎片表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
@Mapper
public interface UserEquipmentFragDao extends BaseMapper<UserEquipmentFragEntity> {

    /**
     * 获取玩家装备碎片
     * @param userId
     * @return
     */
    List<UserEquipmentFragInfoRsp> getUserAllEquipFrag(Long userId);
	
}
