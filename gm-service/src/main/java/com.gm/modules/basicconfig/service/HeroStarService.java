package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroStarEntity;

import java.util.Map;

/**
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
public interface HeroStarService extends IService<HeroStarEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

