package com.gm.modules.user.service.impl;

import com.gm.common.exception.RRException;
import com.gm.modules.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
    @Autowired
    private UserBalanceDetailDao userBalanceDetailDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserBalanceDetailEntity> page = this.page(
                new Query<UserBalanceDetailEntity>().getPage(params),
                new QueryWrapper<UserBalanceDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void insertBalanceDetail(Map<String, Object> map) {
        Date now = new Date();
        UserBalanceDetailEntity balanceDetail = new UserBalanceDetailEntity();
        if ( map.size() > 0 ){
            Long userId = Long.valueOf(map.get("userId").toString());
            BigDecimal amount = new BigDecimal(map.get("amount").toString());
            String tradeType = map.get("tradeType").toString();
            String tradeDesc = map.get("tradeDesc").toString();
            Long sourceId = Long.valueOf(map.get("sourceId").toString());
            balanceDetail.setUserId(userId);
            balanceDetail.setAmount(amount);
            balanceDetail.setTradeType(tradeType);
            balanceDetail.setTradeDesc(tradeDesc);
            balanceDetail.setSourceId(sourceId);
        }
        balanceDetail.setTradeTime(now);
        balanceDetail.setTradeTimeTs(now.getTime());
        boolean b = saveOrUpdate(balanceDetail);
        if (!b) {
            throw new RRException("插入账变失败!");// 插入账变失败
        }
    }

    @Override
    public List<UserBalanceDetailEntity> getUserBalanceDetail(Map<String, Object> map) {
        return userBalanceDetailDao.getUserBalanceDetail(map);
    }

    @Override
    public String queryAgentRebate(UserEntity userEntity) {
        return userBalanceDetailDao.queryAgentRebate(userEntity.getUserId());
    }

}
