package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import com.gm.modules.user.rsp.UserHeroEquipmentWearRsp;
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
public interface UserHeroEquipmentWearService extends IService<UserHeroEquipmentWearEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void insertEquipmentWear(UserHeroEquipmentWearEntity userHeroEquipmentWearEntity);

    /**
     * 获取玩家英雄穿戴中的装备
     * @param userHeroId
     * @return
     */
    List<UserHeroEquipmentWearRsp> getUserWearEQ(@Param("userHeroId") Long userHeroId);
}

