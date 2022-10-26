package com.gm.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * 抽奖订单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-12 19:02:34
 */
public interface TransactionOrderService extends IService<TransactionOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 抽奖订单
     * @param form
     */
    void addOrder(UserEntity user, List gifts, DrawForm form);

    void updateOrder(DrawForm form, List gifts, Map map);

    TransactionOrderEntity getOrderHash(String hash);

    /**
     * 根据用户ID查询该用户消费总额
     * @param userId 用户ID
     * @return 消费总额
     */
    Double queryTotalMoneyByUserId(Long userId);

    /**
     * 根据用户ID查询该用户儿子的消费总额
     * @param userId 用户ID
     * @return 儿子的消费总额
     */
    Double querySonTotalMoneyByFatherId(Long userId);
}

