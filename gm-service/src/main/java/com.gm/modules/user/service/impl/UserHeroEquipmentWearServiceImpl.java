package com.gm.modules.user.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.UserHeroEquipmentWearDao;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import com.gm.modules.user.service.UserHeroEquipmentWearService;


@Service("userHeroEquipmentWearService")
public class UserHeroEquipmentWearServiceImpl extends ServiceImpl<UserHeroEquipmentWearDao, UserHeroEquipmentWearEntity> implements UserHeroEquipmentWearService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String heroName = (String) params.get("heroName");
        String equipName = (String) params.get("equipName");
        String status = (String) params.get("status");
        String equipRarecode = (String) params.get("equipRarecode");
        IPage<UserHeroEquipmentWearEntity> page = this.page(
                new Query<UserHeroEquipmentWearEntity>().getPage(params),
                new QueryWrapper<UserHeroEquipmentWearEntity>()
                    .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                    .eq(StringUtils.isNotBlank(equipRarecode), "B.EQUIP_RARECODE", equipRarecode)
                    .like(StringUtils.isNotBlank(equipName), "B.EQUIP_NAME", equipName)
                    .like(StringUtils.isNotBlank(userName), "E.USER_NAME", userName)
                    .like(StringUtils.isNotBlank(heroName), "C.HERO_NAME", heroName)
        );

        return new PageUtils(page);
    }

}
