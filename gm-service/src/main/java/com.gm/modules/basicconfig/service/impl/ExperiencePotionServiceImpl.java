package com.gm.modules.basicconfig.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.ExperiencePotionDao;
import com.gm.modules.basicconfig.entity.ExperiencePotionEntity;
import com.gm.modules.basicconfig.service.ExperiencePotionService;


@Service("experiencePotionService")
public class ExperiencePotionServiceImpl extends ServiceImpl<ExperiencePotionDao, ExperiencePotionEntity> implements ExperiencePotionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ExperiencePotionEntity> page = this.page(
                new Query<ExperiencePotionEntity>().getPage(params),
                new QueryWrapper<ExperiencePotionEntity>()
        );

        return new PageUtils(page);
    }

}
