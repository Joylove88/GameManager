package com.gm.modules.basicconfig.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.EquipmentFragDao;
import com.gm.modules.basicconfig.entity.EquipmentFragEntity;
import com.gm.modules.basicconfig.service.EquipmentFragService;


@Service("equinpmentFragService")
public class EquipmentFragServiceImpl extends ServiceImpl<EquipmentFragDao, EquipmentFragEntity> implements EquipmentFragService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String equipName = (String) params.get("equipName");
        String equipRarecode = (String) params.get("equipRarecode");
        String status = (String) params.get("status");
        IPage<EquipmentFragEntity> page = this.page(
                new Query<EquipmentFragEntity>().getPage(params),
                new QueryWrapper<EquipmentFragEntity>()
                        .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                        .eq(StringUtils.isNotBlank(equipRarecode), "B.EQUIP_RARECODE", equipRarecode)
                        .like(StringUtils.isNotBlank(equipName), "B.EQUIP_NAME", equipName)
        );

        return new PageUtils(page);
    }

}
