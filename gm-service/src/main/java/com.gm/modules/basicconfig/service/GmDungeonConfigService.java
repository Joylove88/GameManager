package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.GmDungeonConfigEntity;
import com.gm.modules.basicconfig.rsp.DungeonInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 副本配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
public interface GmDungeonConfigService extends IService<GmDungeonConfigEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<GmDungeonConfigEntity> getDungeons();

    /**
     * 获取副本信息
     * @param dungeonConfigEntity
     * @return
     */
    List<DungeonInfoRsp> getDungeonInfo(GmDungeonConfigEntity dungeonConfigEntity);
}

