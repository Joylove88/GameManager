package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.HeroFragEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-27 20:23:36
 */
public interface HeroFragService extends IService<HeroFragEntity> {
    List<HeroFragEntity> queryList(Page pagination, Map<String, Object> map);
    PageUtils queryPage(Map<String, Object> params);
}

