package com.gm.modules.user.dao;

import com.gm.modules.user.entity.UserDailyOutputIncomeRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家每日可产出收益记录表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-04 16:38:24
 */
@Mapper
public interface UserDailyOutputIncomeRecordDao extends BaseMapper<UserDailyOutputIncomeRecordEntity> {

    /**
     * 获取24小时内的记录
     * @param userId 用户ID
     * @return
     */
    List<UserDailyOutputIncomeRecordEntity> getDataFrom24Hr(@Param("userId")Long userId);
}
