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

import java.math.BigDecimal;
import java.util.Map;


@Service("userAccountService")
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
    public boolean updateAccountAdd(Long userId, BigDecimal addMoney) {
        return retBool(userAccountDao.updateAccountAdd(userId,addMoney));
    }

    @Override
    public boolean updateAccountSub(Long userId, BigDecimal subMoney) {
        return retBool(userAccountDao.updateAccountAdd(userId,subMoney));
    }

    @Override
    public UserAccountEntity queryByUserId(Long userId) {
        return baseMapper.selectOne(new QueryWrapper<UserAccountEntity>()
                .eq("user_id",userId)
        );
    }

}
