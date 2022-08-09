package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmUserWithdrawEntity;

import java.util.Map;

/**
 * 提现表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-08-09 19:13:43
 */
public interface GmUserWithdrawService extends IService<GmUserWithdrawEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

