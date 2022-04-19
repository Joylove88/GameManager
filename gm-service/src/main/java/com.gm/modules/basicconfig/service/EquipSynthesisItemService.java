package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;

import java.util.Map;

/**
 * 装备合成公式表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-14 14:07:17
 */
public interface EquipSynthesisItemService extends IService<EquipSynthesisItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

