package com.gm.modules.user.service.impl;

import com.gm.common.exception.RRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.UserBalanceDetailDao;
import com.gm.modules.user.entity.UserBalanceDetailEntity;
import com.gm.modules.user.service.UserBalanceDetailService;


@Service("userBalanceDetailService")
public class UserBalanceDetailServiceImpl extends ServiceImpl<UserBalanceDetailDao, UserBalanceDetailEntity> implements UserBalanceDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserBalanceDetailEntity> page = this.page(
                new Query<UserBalanceDetailEntity>().getPage(params),
                new QueryWrapper<UserBalanceDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void insertBalanceDetail(UserBalanceDetailEntity balanceDetailEntity) {
        Date now = new Date();
        balanceDetailEntity.setTradeTime(now);
        balanceDetailEntity.setTradeTimeTs(now.getTime());
        boolean b = saveOrUpdate(balanceDetailEntity);
        if (!b) {
            throw new RRException("插入账变失败!");// 插入账变失败
        }
    }

}
