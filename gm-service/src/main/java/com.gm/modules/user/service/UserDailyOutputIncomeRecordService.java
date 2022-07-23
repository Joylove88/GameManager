package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserDailyOutputIncomeRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 玩家每日可产出收益记录表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-04 16:38:24
 */
public interface UserDailyOutputIncomeRecordService extends IService<UserDailyOutputIncomeRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取24小时内的记录
     * @param userId 用户ID
     * @return
     */
    List<UserDailyOutputIncomeRecordEntity> getDataFrom24Hr(Long userId);
}

