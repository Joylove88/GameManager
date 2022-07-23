package com.gm.modules.user.service.impl;

import com.gm.modules.order.entity.TransactionOrderEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.GmUserVipLevelDao;
import com.gm.modules.user.entity.GmUserVipLevelEntity;
import com.gm.modules.user.service.GmUserVipLevelService;


@Service("gmUserVipLevelService")
public class GmUserVipLevelServiceImpl extends ServiceImpl<GmUserVipLevelDao, GmUserVipLevelEntity> implements GmUserVipLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmUserVipLevelEntity> page = this.page(
                new Query<GmUserVipLevelEntity>().getPage(params),
                new QueryWrapper<GmUserVipLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void updateUserVipLevel(TransactionOrderEntity order) {

    }

}
