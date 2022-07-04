package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.GmCombatRecordEntity;

import java.util.Map;

/**
 * 战斗记录表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-24 15:46:33
 */
public interface GmCombatRecordService extends IService<GmCombatRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

