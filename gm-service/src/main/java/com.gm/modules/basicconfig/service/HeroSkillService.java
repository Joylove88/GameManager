package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroSkillEntity;
import com.gm.modules.basicconfig.rsp.HeroSkillRsp;

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

    /**
     * 获取英雄技能信息
     * @param map
     * @return
     */
    HeroSkillEntity getHeroSkill(Map<String, Object> map);

    /**
     * 获取英雄技能信息Rsp
     * @param map
     * @return
     */
    HeroSkillRsp getHeroSkillRsp(Map<String, Object> map);
}

