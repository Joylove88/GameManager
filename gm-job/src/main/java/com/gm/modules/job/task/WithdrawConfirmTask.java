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
 * 根据提现哈希ID查询链上，确认该笔出款订单是否成功
 */
@Component("withdrawConfirmTask")
public class WithdrawConfirmTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private GmUserWithdrawService gmUserWithdrawService;

    @Override
    public void run(String params) {
        // 查询未确认出款订单
        List<GmUserWithdrawEntity> gmUserWithdrawEntityList = gmUserWithdrawService.queryOrderByStatus(Constant.WithdrawStatus.ING.getValue());
        for (GmUserWithdrawEntity gmUserWithdrawEntity : gmUserWithdrawEntityList) {
            try {
                // 查询链上转账状态
                gmUserWithdrawService.confirmTransfer(gmUserWithdrawEntity);
            } catch (Exception e) {
                logger.error("WithdrawConfirmTask error.", e);
            }
        }
    }

}
