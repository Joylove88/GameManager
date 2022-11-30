package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmUserVipLevelEntity;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.UseWithdrawReq;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 提现表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-08-09 19:13:43
 */
public interface GmUserWithdrawService extends IService<GmUserWithdrawEntity> {

    PageUtils queryPage(Map<String, Object> params);

    GmUserWithdrawEntity lastWithdraw(UserEntity user);

    void withdraw(UserEntity user, GmUserVipLevelEntity gmUserVipLevel,UseWithdrawReq useWithdrawReq) throws ExecutionException, InterruptedException, IOException;

    boolean haveApplyWithdrawOrder(Long userId);

    PageUtils queryWithdrawList(Long userId, Map<String, Object> params,String cur);

    void checkPass(GmUserWithdrawEntity gmUserWithdraw,Long userId);

    void checkFail(GmUserWithdrawEntity gmUserWithdraw, Long userId);
}

