package com.gm.modules.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.market.dao.GmMarketRecordDao;
import com.gm.modules.market.entity.GmMarketRecordEntity;
import com.gm.modules.market.service.GmMarketRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("gmMarketRecordService")
public class GmMarketRecordServiceImpl extends ServiceImpl<GmMarketRecordDao, GmMarketRecordEntity> implements GmMarketRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmMarketRecordEntity> page = this.page(
                new Query<GmMarketRecordEntity>().getPage(params),
                new QueryWrapper<GmMarketRecordEntity>()
        );

        return new PageUtils(page);
    }

}
