package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmUserVipLevelEntity;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.UseWithdrawReq;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 提现表
 */
public interface GmUserWithdrawService extends IService<GmUserWithdrawEntity> {

    PageUtils queryPage(Map<String, Object> params);

    GmUserWithdrawEntity lastWithdraw(Long userId);

//    void withdraw(UserEntity user, GmUserVipLevelEntity gmUserVipLevel,UseWithdrawReq useWithdrawReq) throws ExecutionException, InterruptedException, IOException;

    boolean haveApplyWithdrawOrder(Long userId);

    PageUtils queryWithdrawList(Long userId, Map<String, Object> params,String cur);

    void checkPass(GmUserWithdrawEntity gmUserWithdraw,Long userId);

    void checkFail(GmUserWithdrawEntity gmUserWithdraw, Long userId);

    /**
     * 根据订单状态查询订单
     * @param status
     * @return
     */
    List<GmUserWithdrawEntity> queryOrderByStatus(Integer status);

    /**
     * 转账
     * @param gmUserWithdrawEntity
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    void transfer(GmUserWithdrawEntity gmUserWithdrawEntity) throws IOException, ExecutionException, InterruptedException;

    void confirmTransfer(GmUserWithdrawEntity gmUserWithdrawEntity) throws IOException;

    /**
     * 客户申请提现
     * @param user
     * @param useWithdrawReq
     */
    void applyWithdraw(UserEntity user, UseWithdrawReq useWithdrawReq);

    /**
     * 对提现订单进行校验
     * @param gmUserWithdrawEntity
     */
    void withdrawApplyVerify(GmUserWithdrawEntity gmUserWithdrawEntity);
}

