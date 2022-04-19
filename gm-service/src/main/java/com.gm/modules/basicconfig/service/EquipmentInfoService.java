package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 装备基础表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 17:20:39
 */
public interface EquipmentInfoService extends IService<EquipmentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean updateEquipJson(Long[] equipIds);

    List<EquipmentInfoEntity> queryList();
}

