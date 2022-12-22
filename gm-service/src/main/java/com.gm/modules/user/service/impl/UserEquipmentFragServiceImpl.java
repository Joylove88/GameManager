package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.UserEquipmentFragDao;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserEquipmentFragEntity;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.rsp.UserEquipmentFragInfoRsp;
import com.gm.modules.user.service.UserEquipmentFragService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("userEquipmentFragService")
public class UserEquipmentFragServiceImpl extends ServiceImpl<UserEquipmentFragDao, UserEquipmentFragEntity> implements UserEquipmentFragService {
    @Autowired
    private UserEquipmentFragDao userEquipmentFragDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String equipName = (String) params.get("equipName");
        String status = (String) params.get("status");
        String fragNum = (String) params.get("fragNum");
        IPage<UserEquipmentFragEntity> page = this.page(
                new Query<UserEquipmentFragEntity>().getPage(params),
                new QueryWrapper<UserEquipmentFragEntity>()
                .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                .eq(StringUtils.isNotBlank(fragNum), "A.USER_EQUIP_FRAG_NUM", fragNum)
                .like(StringUtils.isNotBlank(equipName), "C.EQUIP_NAME", equipName)
                .like(StringUtils.isNotBlank(userName), "D.USER_NAME", userName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<UserEquipmentFragInfoRsp> getUserAllEquipFrag(Long userId) {
        return userEquipmentFragDao.getUserAllEquipFrag(userId);
    }

    @Override
    public PageUtils queryUserEquipmentFrag(Long userId, Map<String, Object> params) {
        IPage<UserEquipmentFragEntity> page = this.page(
                new Query<UserEquipmentFragEntity>().getPage(params),
                new QueryWrapper<UserEquipmentFragEntity>()
                        .eq("A.USER_ID",userId)
                        .eq("A.STATUS",1)

        );
        return new PageUtils(page);
    }

    @Override
    public UserEquipmentFragEntity getUserEquipmentFragById(Long userId ,Long userEquipmentFragId) {
        return userEquipmentFragDao.selectOne(new QueryWrapper<UserEquipmentFragEntity>()
                .eq("status", Constant.enable)
                .eq("user_id",userId)
                .eq("user_equipment_id",userEquipmentFragId)
        );
    }

}
