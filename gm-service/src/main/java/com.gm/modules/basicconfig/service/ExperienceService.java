package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.ExperienceEntity;

import java.util.List;
import java.util.Map;

/**
 * 经验道具
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
public interface ExperienceService extends IService<ExperienceEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取全部经验道具
     * @return
     */
    List<ExperienceEntity> getExpInfos();
}

