package com.gm.modules.basicconfig.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.HeroEquipmentDao;
import com.gm.modules.basicconfig.entity.HeroEquipmentEntity;
import com.gm.modules.basicconfig.service.HeroEquipmentService;


@Service("heroEquipmentService")
public class HeroEquipmentServiceImpl extends ServiceImpl<HeroEquipmentDao, HeroEquipmentEntity> implements HeroEquipmentService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HeroEquipmentEntity> page = this.page(
                new Query<HeroEquipmentEntity>().getPage(params),
                new QueryWrapper<HeroEquipmentEntity>()
        );

        return new PageUtils(page);
    }

}
