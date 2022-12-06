package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.UserAccountDao;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;


@Service("userAccountService")
@Transactional
public class UserAccountServiceImpl extends ServiceImpl<UserAccountDao, UserAccountEntity> implements UserAccountService {
    @Autowired
    private UserAccountDao userAccountDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserAccountEntity> page = this.page(
                new Query<UserAccountEntity>().getPage(params),
                new QueryWrapper<UserAccountEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean updateAccountAdd(Long userId, BigDecimal addMoney, String currency) {
        return retBool(userAccountDao.updateAccountAdd(userId, addMoney, currency));
    }

    @Override
    public boolean updateAccountSub(Long userId, BigDecimal subMoney, String currency) {
        return retBool(userAccountDao.updateAccountSub(userId, subMoney, currency));
    }

    @Override
    public UserAccountEntity queryByUserId(Long userId) {
        return baseMapper.selectOne(new QueryWrapper<UserAccountEntity>()
                .eq("user_id", userId)
        );
    }

    @Override
    public UserAccountEntity queryByUserIdAndCur(Long userId, String currency) {
        return baseMapper.selectOne(new QueryWrapper<UserAccountEntity>()
                .eq("user_id", userId)
                .eq("currency", currency)
        );
    }

    @Override
    public boolean withdrawFreeze(Long userId, BigDecimal freezeMoney,String currency) {
        return retBool(userAccountDao.withdrawFreeze(userId, freezeMoney, currency));
    }

    @Override
    public boolean withdrawThaw(Long userId, BigDecimal thawMoney, String currency) {
        return retBool(userAccountDao.withdrawThaw(userId, thawMoney, currency));
    }

    @Override
    public boolean withdrawSuccess(Long userId, BigDecimal withdrawMoney, String currency) {
        return retBool(userAccountDao.withdrawSuccess(userId, withdrawMoney, currency));
    }

    @Override
    public boolean withdrawFail(Long userId, BigDecimal withdrawMoney, String currency) {
        return retBool(userAccountDao.withdrawThaw(userId, withdrawMoney, currency));
    }

}
