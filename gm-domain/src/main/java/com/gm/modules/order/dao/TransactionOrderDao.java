package com.gm.modules.order.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.order.rsp.TransactionOrderRsp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
     * 获取玩家订单
     * @param page
     * @param eq
     * @return
     */
    IPage<TransactionOrderRsp> pageOrder(IPage<TransactionOrderEntity> page, @Param(Constants.WRAPPER) QueryWrapper<TransactionOrderEntity> eq);

    /**
     * 根据用户ID查询该用户消费总额
     */
    Double queryTotalMoneyByUserId(Long userId);

    /**
     * 根据用户ID查询该用户儿子的消费总额
     */
    Double querySonTotalMoneyByFatherId(Long userId);
}
