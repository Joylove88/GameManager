package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.rsp.UserEquipInfoDetailRsp;
import com.gm.modules.user.rsp.UserEquipInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 玩家装备表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
public interface UserEquipmentService extends IService<UserEquipmentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取装备
     * @param userEquipmentEntity
     * @return
     */
    List<UserEquipInfoRsp> getUserEquip(UserEquipmentEntity userEquipmentEntity);

    /**
     * 获取玩家指定装备
     * @param map
     * @return
     */
    UserEquipInfoDetailRsp getUserEquipById(Map<String, Object> map);

    /**
     * 获取我的装备，分页
     * @param userId
     * @param params
     * @return
     */
    PageUtils queryUserEquipment(Long userId, Map<String, Object> params);

    /**
     * 查询用户的装备
     * @param userEquipmentId
     * @return
     */
    UserEquipmentEntity getUserEquipmentById(Long userId ,Long userEquipmentId);
}

