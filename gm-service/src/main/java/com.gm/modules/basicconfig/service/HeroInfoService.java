package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import com.gm.modules.basicconfig.rsp.HeroInfoDetailRsp;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:04:10
 */
public interface HeroInfoService extends IService<HeroInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<HeroInfoEntity> queryList();

    /**
     * 获取英雄信息集合
     * @return
     */
    List<HeroInfoDetailRsp> getHeroInfoList();
}

