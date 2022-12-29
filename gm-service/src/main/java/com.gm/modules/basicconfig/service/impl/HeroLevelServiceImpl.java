package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.HeroLevelDao;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import com.gm.modules.basicconfig.rsp.HeroLevelRsp;
import com.gm.modules.basicconfig.service.HeroLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("heroLeveService")
public class HeroLevelServiceImpl extends ServiceImpl<HeroLevelDao, HeroLevelEntity> implements HeroLevelService {
    @Autowired
    private HeroLevelDao heroLevelDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HeroLevelEntity> page = this.page(
                new Query<HeroLevelEntity>().getPage(params),
                new QueryWrapper<HeroLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<HeroLevelRsp> getHeroLevels() {
        return heroLevelDao.getHeroLevels();
    }

    @Override
    public HeroLevelRsp getHeroLevelByLvCode(Map<String, Object> map) {
        return heroLevelDao.getHeroLevelByLvCode(map);
    }

}
