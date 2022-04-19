package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroSkillEntity;

import java.util.Map;

/**
 * 英雄技能表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
public interface HeroSkillService extends IService<HeroSkillEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

