package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.HeroFragDao;
import com.gm.modules.basicconfig.service.HeroFragService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.modules.basicconfig.entity.HeroFragEntity;


@Service("heroFragService")
public class HeroFragServiceImpl extends ServiceImpl<HeroFragDao, HeroFragEntity> implements HeroFragService {
    @Autowired
    private HeroFragDao heroFragDao;
    @Override
    public List<HeroFragEntity> queryList(Page pagination, Map<String, Object> map) {
        return heroFragDao.queryList(pagination,map);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HeroFragEntity> page = this.page(
                new Query<HeroFragEntity>().getPage(params),
                new QueryWrapper<HeroFragEntity>()
        );

        return new PageUtils(page);
    }

}
