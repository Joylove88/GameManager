package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;

import java.util.Map;

/**
 * 队伍配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
public interface GmTeamConfigService extends IService<GmTeamConfigEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

