package com.gm.modules.basicconfig.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.GmCombatRecordDao;
import com.gm.modules.basicconfig.entity.GmCombatRecordEntity;
import com.gm.modules.basicconfig.service.GmCombatRecordService;


@Service("gmCombatRecordService")
public class GmCombatRecordServiceImpl extends ServiceImpl<GmCombatRecordDao, GmCombatRecordEntity> implements GmCombatRecordService {
    @Autowired
    private GmCombatRecordDao combatRecordDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmCombatRecordEntity> page = this.page(
                new Query<GmCombatRecordEntity>().getPage(params),
                new QueryWrapper<GmCombatRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<GmCombatRecordEntity> getCombatRecordNow(Map<String, Object> params) {
        return combatRecordDao.getCombatRecordNow(params);
    }

}
