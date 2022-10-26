package com.gm.modules.basicconfig.service.impl;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.modules.basicconfig.dao.StarInfoDao;
import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.gm.modules.basicconfig.service.StarInfoService;


@Service("starInfoService")
public class StarInfoServiceImpl extends ServiceImpl<StarInfoDao, StarInfoEntity> implements StarInfoService {
    @Autowired
    private StarInfoDao starInfoDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StarInfoEntity> page = this.page(
                new Query<StarInfoEntity>().getPage(params),
                new QueryWrapper<StarInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<StarInfoEntity> getStarInfoPro() {
        return starInfoDao.getStarInfoPro();
    }

}
