package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.HeroStarDao;
import com.gm.modules.basicconfig.entity.HeroStarEntity;
import com.gm.modules.basicconfig.service.HeroStarService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("heroStarService")
public class HeroStarServiceImpl extends ServiceImpl<HeroStarDao, HeroStarEntity> implements HeroStarService {
    @Autowired
    private HeroStarDao heroStarDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String starCode = (String) params.get("starCode");
        String heroName = (String) params.get("heroName");
        IPage<HeroStarEntity> page = this.page(
                new Query<HeroStarEntity>().getPage(params),
                new QueryWrapper<HeroStarEntity>()
                        .eq(StringUtils.isNotBlank(starCode), "B.GM_STAR_CODE", starCode)
                        .like(StringUtils.isNotBlank(heroName), "C.HERO_NAME", heroName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<HeroStarEntity> getHeroStars() {
        return heroStarDao.getHeroStars();
    }

    @Override
    public List<HeroStarEntity> getRangeHeroStars(Map<String, Object> map) {
        return heroStarDao.getRangeHeroStars(map);
    }

}
