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

    /**
     * 冻结账户余额
     * @param userId 用户ID
     * @param freezeMoney 金额
     * @param currency 提现类型
     */
    boolean withdrawFreeze(Long userId, BigDecimal freezeMoney,String currency);

    /**
     * 解冻账户余额
     * @param userId 用户ID
     * @param thawMoney 金额
     * @param currency 提现类型
     */
    boolean withdrawThaw(Long userId, BigDecimal thawMoney, String currency);

    /**
     * 提现成功
     */
    boolean withdrawSuccess(Long userId, BigDecimal withdrawMoney, String currency);

    /**
     * 提现失败
     */
    boolean withdrawFail(Long userId, BigDecimal withdrawMoney, String currency);
}

