package com.gm.modules.order.dao;

import com.gm.modules.order.entity.TransactionOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖订单
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-12 19:02:34
 */
@Mapper
public interface TransactionOrderDao extends BaseMapper<TransactionOrderEntity> {

    /**
     * 根据用户ID查询该用户消费总额
     */
    Double queryTotalMoneyByUserId(Long userId);

    /**
     * 根据用户ID查询该用户儿子的消费总额
     */
    Double querySonTotalMoneyByFatherId(Long userId);
}
