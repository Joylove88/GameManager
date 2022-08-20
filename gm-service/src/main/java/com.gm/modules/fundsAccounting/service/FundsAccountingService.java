package com.gm.modules.fundsAccounting.service;

import com.gm.common.utils.Arith;
import com.gm.modules.sys.service.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 资金记账业务类
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("fundsAccountingService")
public class FundsAccountingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FundsAccountingService.class);
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 资金池记账（增加）
     * 主（动态获取主资金池TOKEN=BUSD余额）
     * 用户（金币兑换，副本产出，代理返点）
     * 副本（(主资金池*0.7)-团队资金池-用户资金池）
     * 回购（消费金额*0.2）只增加
     * 团队（每次抽奖的5%抽成，游戏内部的消耗）只增加
     * @param poolName
     * @param value
     */
    public void setCashPoolAdd(String poolName, BigDecimal value){
        // 获取资金池余额
        String balance = sysConfigService.getValue(poolName);
        value = Arith.add(new BigDecimal(balance), value);
        sysConfigService.updateValueByKey(poolName, String.valueOf(value));
    }

    /**
     * 资金池记账（减少）
     * 用户（金币抽奖、命运板块、提款、强化等一些游戏内金币消费）
     * @param poolName
     * @param value
     */
    public void setCashPoolSub(String poolName, BigDecimal value){
        // 获取资金池余额
        String balance = sysConfigService.getValue(poolName);
        value = Arith.subtract(new BigDecimal(balance), value);
        sysConfigService.updateValueByKey(poolName, String.valueOf(value));
    }
}

