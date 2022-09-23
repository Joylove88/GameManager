package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserBalanceDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户资金明细：记录余额变动
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
public interface UserBalanceDetailService extends IService<UserBalanceDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 更新账户金额
     */
    void insertBalanceDetail(Map<String, Object> params);

    /**
     * 获取账变明细
     * @param map
     * @return
     */
    List<UserBalanceDetailEntity> getUserBalanceDetail(Map<String, Object> map);
}

