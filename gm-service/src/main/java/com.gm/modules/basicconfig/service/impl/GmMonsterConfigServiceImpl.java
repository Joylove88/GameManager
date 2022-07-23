package com.gm.modules.basicconfig.service.impl;

import com.gm.modules.basicconfig.rsp.MonsterInfoRsp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.GmMonsterConfigDao;
import com.gm.modules.basicconfig.entity.GmMonsterConfigEntity;
import com.gm.modules.basicconfig.service.GmMonsterConfigService;


@Service("gmMonsterConfigService")
public class GmMonsterConfigServiceImpl extends ServiceImpl<GmMonsterConfigDao, GmMonsterConfigEntity> implements GmMonsterConfigService {
    @Autowired
    private GmMonsterConfigDao monsterConfigDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String monsterName = (String) params.get("monsterName");
        String dungeonName = (String) params.get("dungeonName");
        String monsterLevel = (String) params.get("monsterLevel");
        String status = (String) params.get("status");
        IPage<GmMonsterConfigEntity> page = this.page(
                new Query<GmMonsterConfigEntity>().getPage(params),
                new QueryWrapper<GmMonsterConfigEntity>()
                        .eq(StringUtils.isNotBlank(status), "a.STATUS", status)
                        .eq(StringUtils.isNotBlank(monsterLevel), "a.MONSTER_LEVEL", monsterLevel)
                        .like(StringUtils.isNotBlank(dungeonName), "b.DUNGEON_NAME", dungeonName)
                        .like(StringUtils.isNotBlank(monsterName), "a.MONSTER_NAME", monsterName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<MonsterInfoRsp> getMonsterInfo(GmMonsterConfigEntity monsterConfigEntity) {
        return monsterConfigDao.getMonsterInfo(monsterConfigEntity);
    }

}
