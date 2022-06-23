package com.gm.modules.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.GmMiningInfoDao;
import com.gm.modules.user.entity.GmMiningInfoEntity;
import com.gm.modules.user.service.GmMiningInfoService;


@Service("gmMiningInfoService")
public class GmMiningInfoServiceImpl extends ServiceImpl<GmMiningInfoDao, GmMiningInfoEntity> implements GmMiningInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmMiningInfoEntity> page = this.page(
                new Query<GmMiningInfoEntity>().getPage(params),
                new QueryWrapper<GmMiningInfoEntity>()
        );

        return new PageUtils(page);
    }

}
