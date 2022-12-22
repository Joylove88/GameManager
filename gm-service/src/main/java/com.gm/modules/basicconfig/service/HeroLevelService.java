package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import com.gm.modules.basicconfig.rsp.HeroLevelRsp;

import java.util.List;
import java.util.Map;

/**
 * 英雄等级表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-03 19:44:44
 */
public interface HeroLevelService extends IService<HeroLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取英雄等级表
     * @return
     */
    List<HeroLevelRsp> getHeroLevels();
}

