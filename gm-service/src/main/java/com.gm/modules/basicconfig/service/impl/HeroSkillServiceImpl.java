package com.gm.modules.basicconfig.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.HeroSkillDao;
import com.gm.modules.basicconfig.entity.HeroSkillEntity;
import com.gm.modules.basicconfig.service.HeroSkillService;


@Service("heroSkillService")
public class HeroSkillServiceImpl extends ServiceImpl<HeroSkillDao, HeroSkillEntity> implements HeroSkillService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String heroName = (String) params.get("heroName");
        String status = (String) params.get("status");
        IPage<HeroSkillEntity> page = this.page(
                new Query<HeroSkillEntity>().getPage(params),
                new QueryWrapper<HeroSkillEntity>()
                        .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                        .like(StringUtils.isNotBlank(heroName), "B.HERO_NAME", heroName)
        );

        return new PageUtils(page);
    }

}
