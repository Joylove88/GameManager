package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.ExperienceDao;
import com.gm.modules.basicconfig.entity.ExperienceEntity;
import com.gm.modules.basicconfig.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("experienceService")
public class ExperienceServiceImpl extends ServiceImpl<ExperienceDao, ExperienceEntity> implements ExperienceService {
    @Autowired
    private ExperienceDao experienceDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ExperienceEntity> page = this.page(
                new Query<ExperienceEntity>().getPage(params),
                new QueryWrapper<ExperienceEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ExperienceEntity> getExpInfos() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Constant.enable);
        return experienceDao.getExpInfos(map);
    }

}
