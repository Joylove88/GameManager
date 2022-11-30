package com.gm.modules.user.dao;

import com.gm.modules.user.entity.UserAccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 用户资金账户
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@Mapper
public interface UserAccountDao extends BaseMapper<UserAccountEntity> {
    /**
     * 更新账户金额
     *
     * @param userId 用户ID
     * @param addMoney   增加的金额
     * @return 影响的行数
     */
    Integer updateAccountAdd(@Param("userId") Long userId, @Param("addMoney") BigDecimal addMoney, @Param("currency") String currency);
    /**
     * 更新账户金额
     *
     * @param userId 用户ID
     * @param subMoney   减少的金额
     * @return 影响的行数
     */
    Integer updateAccountSub(@Param("userId") Long userId, @Param("subMoney") BigDecimal subMoney, @Param("currency") String currency);

    /**
     * 冻结账户余额
     * @param userId 用户ID
     * @param freezeMoney 冻结金额
     * @param currency 类型
     */
    Integer withdrawFreeze(@Param("userId") Long userId, @Param("freezeMoney") BigDecimal freezeMoney,@Param("currency") String currency);

    /**
     * 冻结账户余额
     * @param userId 用户ID
     * @param thawMoney 冻结金额
     * @param currency 类型
     */
    Integer withdrawThaw(@Param("userId")Long userId, @Param("thawMoney") BigDecimal thawMoney,@Param("currency")  String currency);
}
