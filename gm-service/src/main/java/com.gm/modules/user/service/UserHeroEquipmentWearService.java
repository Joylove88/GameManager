package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;

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
}

