package com.gm.modules.basicconfig.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.GmDungeonEventDao;
import com.gm.modules.basicconfig.entity.GmDungeonEventEntity;
import com.gm.modules.basicconfig.service.GmDungeonEventService;


@Service("gmDungeonEventService")
public class GmDungeonEventServiceImpl extends ServiceImpl<GmDungeonEventDao, GmDungeonEventEntity> implements GmDungeonEventService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmDungeonEventEntity> page = this.page(
                new Query<GmDungeonEventEntity>().getPage(params),
                new QueryWrapper<GmDungeonEventEntity>()
        );

        return new PageUtils(page);
    }

}
