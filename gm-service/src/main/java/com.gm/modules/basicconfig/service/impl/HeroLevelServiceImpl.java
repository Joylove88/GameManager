package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.HeroLevelDao;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import com.gm.modules.basicconfig.service.HeroLevelService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("heroLeveService")
public class HeroLevelServiceImpl extends ServiceImpl<HeroLevelDao, HeroLevelEntity> implements HeroLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HeroLevelEntity> page = this.page(
                new Query<HeroLevelEntity>().getPage(params),
                new QueryWrapper<HeroLevelEntity>()
        );

        return new PageUtils(page);
    }

}
