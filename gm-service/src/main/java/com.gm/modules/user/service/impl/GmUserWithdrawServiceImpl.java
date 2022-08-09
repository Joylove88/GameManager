package com.gm.modules.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.GmUserWithdrawDao;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.service.GmUserWithdrawService;


@Service("gmUserWithdrawService")
public class GmUserWithdrawServiceImpl extends ServiceImpl<GmUserWithdrawDao, GmUserWithdrawEntity> implements GmUserWithdrawService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmUserWithdrawEntity> page = this.page(
                new Query<GmUserWithdrawEntity>().getPage(params),
                new QueryWrapper<GmUserWithdrawEntity>()
        );

        return new PageUtils(page);
    }

}
