package com.gm.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.order.dao.TransactionOrderDao;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.UserEntity;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("transactionOrderService")
public class TransactionOrderServiceImpl extends ServiceImpl<TransactionOrderDao, TransactionOrderEntity> implements TransactionOrderService {
    @Autowired TransactionOrderDao transactionOrderDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TransactionOrderEntity> page = this.page(
                new Query<TransactionOrderEntity>().getPage(params),
                new QueryWrapper<TransactionOrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void addOrder(UserEntity user, List gifts, DrawForm form) {
        TransactionOrderEntity orderEntity = new TransactionOrderEntity();
        Date date = new Date();
        // 为加密货币类型时 订单状态为待处理，并且插入hash值。否则为金币类型时 订单状态为成功，并且插入用户ID
        if (Constant.enable.equals(form.getCurType())){
            orderEntity.setStatus(Constant.disabled);// 订单状态：默认0：待处理
            orderEntity.setTransactionHash(form.getTransactionHash());
        } else {
            JSONArray jsonArray = JSONArray.fromObject(gifts);
            orderEntity.setItemData(jsonArray.toString());
            orderEntity.setStatus(Constant.enable);// 订单状态：1：成功
            orderEntity.setGmUserId(user.getUserId());
        }
        orderEntity.setCurrencyType(form.getCurType());
        orderEntity.setLottyType(form.getDrawType());
        orderEntity.setItemType(form.getItemType());// 物品类型('1':英雄，'2':装备，'3':药水)
        orderEntity.setCreateTime(date);
        orderEntity.setCreateTimeTs(date.getTime());
        transactionOrderDao.insert(orderEntity);
    }

    @Override
    public void updateOrder(DrawForm form, List gifts, Map map) {
        Date date = new Date();
        String transactionHash = form.getTransactionHash();

        // 实例化订单
        TransactionOrderEntity order = new TransactionOrderEntity();
        JSONArray jsonArray = JSONArray.fromObject(gifts);
        order.setItemData(jsonArray.toString());
        order.setUpdateTime(date);
        order.setUpdateTimeTs(date.getTime());
        if (map.size() > 0){
            Long blockNumber = Long.valueOf(map.get("blockNumber").toString());
            Long gasUsed = Long.valueOf(map.get("gasUsed").toString());
            Long userId = Long.valueOf(map.get("userId").toString());
            String status = map.get("status").toString();
            order.setStatus(status);
            order.setBlockNumber(blockNumber);
            order.setTransactionGasFee(gasUsed);
            order.setGmUserId(userId);
        }
        QueryWrapper<TransactionOrderEntity> wrapper = new QueryWrapper<TransactionOrderEntity>()
                .eq("TRANSACTION_HASH",transactionHash);
        transactionOrderDao.update(order, wrapper);
    }

    @Override
    public TransactionOrderEntity getOrderHash(String hash) {
        // 通过交易hash查找订单
        TransactionOrderEntity orderEntity = transactionOrderDao.selectOne(new QueryWrapper<TransactionOrderEntity>()
                .eq(StringUtils.isNotBlank(hash),"TRANSACTION_HASH",hash)
        );
        return orderEntity;
    }


}
