package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.GmDungeonEventEntity;

import java.util.Map;

/**
 * 副本事件表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-10 15:57:41
 */
public interface GmDungeonEventService extends IService<GmDungeonEventEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

