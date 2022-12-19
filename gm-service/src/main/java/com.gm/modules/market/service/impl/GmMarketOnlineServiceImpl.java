package com.gm.modules.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.market.dao.GmMarketOnlineDao;
import com.gm.modules.market.entity.GmMarketOnlineEntity;
import com.gm.modules.market.service.GmMarketOnlineService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("gmMarketOnlineService")
public class GmMarketOnlineServiceImpl extends ServiceImpl<GmMarketOnlineDao, GmMarketOnlineEntity> implements GmMarketOnlineService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmMarketOnlineEntity> page = this.page(
                new Query<GmMarketOnlineEntity>().getPage(params),
                new QueryWrapper<GmMarketOnlineEntity>()
        );

        return new PageUtils(page);
    }

}
