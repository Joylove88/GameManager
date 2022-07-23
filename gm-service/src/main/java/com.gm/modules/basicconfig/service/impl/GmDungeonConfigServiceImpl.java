package com.gm.modules.basicconfig.service.impl;

import com.gm.common.utils.Constant;
import com.gm.modules.basicconfig.rsp.DungeonInfoRsp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.GmDungeonConfigDao;
import com.gm.modules.basicconfig.entity.GmDungeonConfigEntity;
import com.gm.modules.basicconfig.service.GmDungeonConfigService;


@Service("gmDungeonConfigService")
public class GmDungeonConfigServiceImpl extends ServiceImpl<GmDungeonConfigDao, GmDungeonConfigEntity> implements GmDungeonConfigService {
    @Autowired
    private GmDungeonConfigDao dungeonConfigDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String status = (String) params.get("status");
        IPage<GmDungeonConfigEntity> page = this.page(
                new Query<GmDungeonConfigEntity>().getPage(params),
                new QueryWrapper<GmDungeonConfigEntity>()
                        .eq(StringUtils.isNotBlank(status), "a.STATUS", status)
        );

        return new PageUtils(page);
    }

    @Override
    public List<GmDungeonConfigEntity> getDungeons() {
        Map<String, Object> map = new HashMap<>();
        map.put("STATUS", Constant.enable);
        return dungeonConfigDao.selectByMap(map);
    }

    @Override
    public List<DungeonInfoRsp> getDungeonInfo(GmDungeonConfigEntity dungeonConfigEntity) {
        return dungeonConfigDao.getDungeonInfo(dungeonConfigEntity);
    }


}
