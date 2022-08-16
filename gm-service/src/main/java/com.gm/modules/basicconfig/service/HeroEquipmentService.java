package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroEquipmentEntity;

import java.util.List;
import java.util.Map;

/**
 * 英雄装备栏表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-17 18:07:31
 */
public interface HeroEquipmentService extends IService<HeroEquipmentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取英雄装备栏
     * @param map
     * @return
     */
    List<HeroEquipmentEntity> getHeroEquipments(Map<String, Object> map);
}

