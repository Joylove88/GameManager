package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.rsp.UserEquipInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 玩家装备表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
@Mapper
public interface UserEquipmentDao extends BaseMapper<UserEquipmentEntity> {

    /**
     * 获取装备
     * @param userId
     * @return
     */
    List<UserEquipInfoRsp> getUserAllEquip(Long userId);
}
