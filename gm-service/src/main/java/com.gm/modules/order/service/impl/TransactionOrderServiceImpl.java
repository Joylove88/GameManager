package com.gm.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.fundsAccounting.service.FundsAccountingService;
import com.gm.modules.order.dao.TransactionOrderDao;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.rsp.TransactionOrderRsp;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.SummonReq;
import com.gm.modules.user.service.GmUserVipLevelService;
import com.gm.modules.user.service.UserBalanceDetailService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("transactionOrderService")
public class TransactionOrderServiceImpl extends ServiceImpl<TransactionOrderDao, TransactionOrderEntity> implements TransactionOrderService {
    @Autowired
    TransactionOrderDao transactionOrderDao;
    @Autowired
    GmUserVipLevelService gmUserVipLevelService;
    @Autowired
    private FundsAccountingService fundsAccountingService;
    @Autowired
    private UserBalanceDetailService userBalanceDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String txHash = (String) params.get("txHash");
        String summonType = (String) params.get("summonType");
        String summonNum = (String) params.get("summonNum");
        String currencyType = (String) params.get("currencyType");
        IPage<TransactionOrderEntity> page = this.page(
                new Query<TransactionOrderEntity>().getPage(params),
                new QueryWrapper<TransactionOrderEntity>()
                        .eq(StringUtils.isNotBlank(txHash), "A.HASH", txHash)
                        .eq(StringUtils.isNotBlank(txHash), "A.SUMMON_TYPE", summonType)
                        .eq(StringUtils.isNotBlank(txHash), "A.SUMMON_NUM", summonNum)
                        .eq(StringUtils.isNotBlank(txHash), "A.CURRENCY_TYPE", currencyType)
        );

        return new PageUtils(page);
    }

    @Override
    public void addOrder(UserEntity user, List gifts, SummonReq form) {
        TransactionOrderEntity order = new TransactionOrderEntity();
        Date date = new Date();
        // 为加密货币类型时 订单状态为待处理，并且插入hash值。否则为金币类型时 订单状态为成功，并且插入用户ID
        if (Constant.CurrencyType._CRYPTO.getValue().equals(form.getCurType())) {
            order.setStatus(Constant.disabled);// 订单状态：默认0：待处理
            order.setHash(form.getTransactionHash());
        } else {
            JSONArray jsonArray = JSONArray.fromObject(gifts);
            order.setOrderFee(form.getOrderFee());
            order.setRealFee(form.getRealFee());
            order.setItemData(jsonArray.toString());
            order.setStatus(Constant.enable);// 订单状态：1：成功
            order.setUserId(user.getUserId());
            // 用户池出账
            fundsAccountingService.setCashPoolSub(Constant.CashPool._USER.getValue(), order.getRealFee());// 扣除本次召唤金币数量
            // 副本池入账
            BigDecimal dungeonFee = Arith.multiply(order.getRealFee(), Constant.CashPoolScale._DUNGEON.getValue());// 获取该订单金额的75%
            fundsAccountingService.setCashPoolAdd(Constant.CashPool._DUNGEON.getValue(), dungeonFee);
            // 回购池入账
            BigDecimal repoFee = Arith.multiply(order.getRealFee(), Constant.CashPoolScale._REPO.getValue());// 获取该订单金额的20%
            fundsAccountingService.setCashPoolAdd(Constant.CashPool._REPO.getValue(), repoFee);
            // 团队抽成池入账
            BigDecimal teamFee = Arith.multiply(order.getRealFee(), Constant.CashPoolScale._TEAM.getValue());// 获取该订单金额的5%
            fundsAccountingService.setCashPoolAdd(Constant.CashPool._TEAM.getValue(), teamFee);
        }
        order.setCurrencyType(form.getCurType());// 货币类型('0':金币，'1':加密货币)
        order.setSummonType(form.getSummonType());// 召唤类型('1':英雄，'2':装备，'3':经验道具)
        order.setSummonNum(form.getSummonNum());// 召唤次数
        order.setCreateTime(date);
        order.setCreateTimeTs(date.getTime());
        transactionOrderDao.insert(order);

        // 金币召唤时插入账变明细
        if (Constant.CurrencyType._GOLD_COINS.getValue().equals(form.getCurType())) {
            String tradeType = "";
            if (form.getSummonType().equals(Constant.SummonType.HERO.getValue())) {
                tradeType = Constant.TradeType.DRAW_HERO.getValue();
            } else if (form.getSummonType().equals(Constant.SummonType.EQUIPMENT.getValue())) {
                tradeType = Constant.TradeType.DRAW_EQUIP.getValue();
            } else if (form.getSummonType().equals(Constant.SummonType.EXPERIENCE.getValue())) {
                tradeType = Constant.TradeType.DRAW_EXP.getValue();
            }
            Map<String, Object> balanceMap = new HashMap<>();
            balanceMap.put("userId", user.getUserId());
            balanceMap.put("orderFee", order.getOrderFee());
            balanceMap.put("realFee", order.getRealFee());
            balanceMap.put("tradeType", tradeType);
            balanceMap.put("tradeDesc", "召唤");
            balanceMap.put("sourceId", order.getId());// 召唤订单ID
            userBalanceDetailService.insertBalanceDetail(balanceMap);
        }

    }

    @Override
    public void updateOrder(SummonReq form, List gifts, Map map) {
        Date date = new Date();
        String transactionHash = form.getTransactionHash();

        // 实例化订单
        TransactionOrderEntity order = new TransactionOrderEntity();
        JSONArray jsonArray = JSONArray.fromObject(gifts);
        order.setItemData(jsonArray.toString());
        order.setUpdateTime(date);
        order.setUpdateTimeTs(date.getTime());
        // 加密货币类型订单
        if (map.size() > 0) {
            Long blockNumber = null == map.get("blockNumber") ? null : Long.valueOf(map.get("blockNumber").toString());
            BigDecimal gasUsed = null == map.get("gasUsed") ? BigDecimal.ZERO : new BigDecimal(map.get("gasUsed").toString());
            BigDecimal orderFee = null == map.get("orderFee") ? BigDecimal.ZERO : new BigDecimal(map.get("orderFee").toString());
            BigDecimal realFee = null == map.get("realFee") ? BigDecimal.ZERO : new BigDecimal(map.get("realFee").toString());
            Long userId = null == map.get("userId") ? null : Long.valueOf(map.get("userId").toString());
            String status = null == map.get("status") ? "3" : map.get("status").toString();
            order.setStatus(status);
            order.setBlockNumber(blockNumber);
            order.setGasFee(gasUsed);
            order.setUserId(userId);
            order.setOrderFee(orderFee);
            order.setRealFee(realFee);
        }

        // 通过HASH更新订单
        QueryWrapper<TransactionOrderEntity> wrapper = new QueryWrapper<TransactionOrderEntity>()
                .eq("HASH", transactionHash);
        transactionOrderDao.update(order, wrapper);

        // 如果召唤成功，则升级用户消费等级
        if (Constant.enable.equals(order.getStatus())) {
            gmUserVipLevelService.updateUserVipLevel(order);
        }

    }

    @Override
    public TransactionOrderEntity getOrderHash(String hash) {
        // 通过交易hash查找订单
        TransactionOrderEntity orderEntity = transactionOrderDao.selectOne(new QueryWrapper<TransactionOrderEntity>()
                .eq(StringUtils.isNotBlank(hash), "HASH", hash)
        );
        return orderEntity;
    }

    @Override
    public PageUtils queryUserOrder(Long userId, Map<String, Object> params) {
        IPage<TransactionOrderRsp> page = transactionOrderDao.pageOrder(
                new Query<TransactionOrderRsp>().getPage(params),
                new QueryWrapper<TransactionOrderRsp>()
                        .eq("A.USER_ID", userId)
        );
        return new PageUtils(page);
    }

    @Override
    public Double queryTotalMoneyByUserId(Long userId) {
        return transactionOrderDao.queryTotalMoneyByUserId(userId);
    }

    @Override
    public Double querySonTotalMoneyByFatherId(Long userId) {
        return transactionOrderDao.querySonTotalMoneyByFatherId(userId);
    }


}
