package com.gm.modules.basicconfig.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.ProbabilityEntity;

import java.util.Map;

/**
 * 抽奖概率配置
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-08 16:26:15
 */
public interface ProbabilityService extends IService<ProbabilityEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

