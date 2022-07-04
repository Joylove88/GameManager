package com.gm.modules.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.UserDailyOutputIncomeRecordDao;
import com.gm.modules.user.entity.UserDailyOutputIncomeRecordEntity;
import com.gm.modules.user.service.UserDailyOutputIncomeRecordService;


@Service("userDailyOutputIncomeRecordService")
public class UserDailyOutputIncomeRecordServiceImpl extends ServiceImpl<UserDailyOutputIncomeRecordDao, UserDailyOutputIncomeRecordEntity> implements UserDailyOutputIncomeRecordService {
    @Autowired
    private UserDailyOutputIncomeRecordDao userDailyOutputIncomeRecordDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserDailyOutputIncomeRecordEntity> page = this.page(
                new Query<UserDailyOutputIncomeRecordEntity>().getPage(params),
                new QueryWrapper<UserDailyOutputIncomeRecordEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取24小时内的记录
     * @param userId 用户ID
     * @param teamId 队伍ID
     * @return
     */
    @Override
    public List<UserDailyOutputIncomeRecordEntity> getDataFrom24Hr(Long userId, Long teamId) {
        return userDailyOutputIncomeRecordDao.getDataFrom24Hr(userId, teamId);
    }

}
