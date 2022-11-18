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
    boolean updateAccountAdd(Long userId, BigDecimal addMoney, String currency);

    /**
     * 给用户减少金额
     *
     * @param userId 用户ID
     * @param subMoney   金额
     * @return boolean
     */
    boolean updateAccountSub(Long userId, BigDecimal subMoney, String currency);

    /**
     * 根据用户ID查询账户
     * @param userId 用户ID
     * @return 用户账户
     */
    UserAccountEntity queryByUserId(Long userId);

    UserAccountEntity queryByUserIdAndCur(Long userId, String currency);
}

