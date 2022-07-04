package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserAccountEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户资金账户
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
public interface UserAccountService extends IService<UserAccountEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 给用户添加金额
     *
     * @param userId 用户ID
     * @param addMoney   金额
     * @return boolean
     */
    boolean updateAccountAdd(Long userId, BigDecimal addMoney);
}

