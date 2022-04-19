package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserAccountEntity;

import java.util.Map;

/**
 * 用户资金账户
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
public interface UserAccountService extends IService<UserAccountEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

