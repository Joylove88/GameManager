package com.gm.modules.job.task;

import com.gm.common.utils.Constant;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.service.GmUserWithdrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 提现转账
 */
@Component("withdrawTask")
public class WithdrawTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private GmUserWithdrawService gmUserWithdrawService;

    @Override
    public void run(String params) {
        // 查询未确认出款订单
        List<GmUserWithdrawEntity> gmUserWithdrawEntityList = gmUserWithdrawService.queryOrderByStatus(Constant.WithdrawStatus.CHECK_SUCCESS.getValue());
        for (GmUserWithdrawEntity gmUserWithdrawEntity : gmUserWithdrawEntityList) {
            try {
                // 调用智能合约进行转账
                gmUserWithdrawService.transfer(gmUserWithdrawEntity);
            } catch (Exception e) {
                logger.error("WithdrawTask error.", e);
            }
        }
    }

}
