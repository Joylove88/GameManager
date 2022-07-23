package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.GmMonsterConfigEntity;
import com.gm.modules.basicconfig.rsp.MonsterInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 怪物配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
public interface GmMonsterConfigService extends IService<GmMonsterConfigEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取怪物信息
     * @param monsterConfigEntity
     * @return
     */
    List<MonsterInfoRsp> getMonsterInfo(GmMonsterConfigEntity monsterConfigEntity);
}

