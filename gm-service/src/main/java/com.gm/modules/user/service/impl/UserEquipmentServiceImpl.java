package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.UserEquipmentDao;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.rsp.UserEquipInfoRsp;
import com.gm.modules.user.service.UserEquipmentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("userEquipmentService")
public class UserEquipmentServiceImpl extends ServiceImpl<UserEquipmentDao, UserEquipmentEntity> implements UserEquipmentService {
    @Autowired
    private UserEquipmentDao userEquipmentDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String equipName = (String) params.get("equipName");
        String status = (String) params.get("status");
        String equipRarecode = (String) params.get("equipRarecode");
        IPage<UserEquipmentEntity> page = this.page(
                new Query<UserEquipmentEntity>().getPage(params),
                new QueryWrapper<UserEquipmentEntity>()
                        .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                        .eq(StringUtils.isNotBlank(equipRarecode), "B.EQUIP_RARECODE", equipRarecode)
                        .like(StringUtils.isNotBlank(equipName), "B.EQUIP_NAME", equipName)
                        .like(StringUtils.isNotBlank(userName), "C.USER_NAME", userName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<UserEquipInfoRsp> getUserEquip(UserEquipmentEntity userEquipmentEntity) {
        return userEquipmentDao.getUserEquip(userEquipmentEntity);
    }

}
