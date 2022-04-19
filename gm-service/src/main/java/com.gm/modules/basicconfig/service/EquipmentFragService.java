package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.EquipmentFragEntity;

import java.util.Map;

/**
 * 装备碎片表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 19:09:21
 */
public interface EquipmentFragService extends IService<EquipmentFragEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

