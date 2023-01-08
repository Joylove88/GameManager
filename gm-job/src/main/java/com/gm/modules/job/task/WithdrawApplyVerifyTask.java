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
 * 提现订单校验任务
 */
@Component("withdrawApplyVerifyTask")
public class WithdrawApplyVerifyTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private GmUserWithdrawService gmUserWithdrawService;

    @Override
    public void run(String params) {
        // 查询提现申请订单，对订单进行校验
        List<GmUserWithdrawEntity> gmUserWithdrawEntityList = gmUserWithdrawService.queryOrderByStatus(Constant.WithdrawStatus.APPLY.getValue());
        for (GmUserWithdrawEntity gmUserWithdrawEntity : gmUserWithdrawEntityList) {
            try {
                gmUserWithdrawService.withdrawApplyVerify(gmUserWithdrawEntity);
            } catch (Exception e) {
                logger.error("WithdrawApplyVerifyTask error.", e);
            }
        }
    }

}
