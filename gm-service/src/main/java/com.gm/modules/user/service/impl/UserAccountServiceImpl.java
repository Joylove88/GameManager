package com.gm.modules.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.UserAccountDao;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.service.UserAccountService;


@Service("userAccountService")
public class UserAccountServiceImpl extends ServiceImpl<UserAccountDao, UserAccountEntity> implements UserAccountService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserAccountEntity> page = this.page(
                new Query<UserAccountEntity>().getPage(params),
                new QueryWrapper<UserAccountEntity>()
        );

        return new PageUtils(page);
    }

}
