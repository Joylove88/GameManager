package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.ExperiencePotionDao;
import com.gm.modules.basicconfig.entity.ExperiencePotionEntity;
import com.gm.modules.basicconfig.service.ExperiencePotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("experiencePotionService")
public class ExperiencePotionServiceImpl extends ServiceImpl<ExperiencePotionDao, ExperiencePotionEntity> implements ExperiencePotionService {
    @Autowired
    private ExperiencePotionDao experiencePotionDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ExperiencePotionEntity> page = this.page(
                new Query<ExperiencePotionEntity>().getPage(params),
                new QueryWrapper<ExperiencePotionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ExperiencePotionEntity> getExpInfos() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Constant.enable);
        return experiencePotionDao.getExpInfos(map);
    }

}
