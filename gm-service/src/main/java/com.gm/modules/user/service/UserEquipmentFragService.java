package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserEquipmentFragEntity;
import com.gm.modules.user.rsp.UserEquipmentFragInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 玩家装备碎片表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
public interface UserEquipmentFragService extends IService<UserEquipmentFragEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取玩家装备碎片
     * @param userId
     * @return
     */
    List<UserEquipmentFragInfoRsp> getUserAllEquipFrag(Long userId);

    List<UserEquipmentFragEntity> queryUserEquipmentFrag(UserEntity user);
}

