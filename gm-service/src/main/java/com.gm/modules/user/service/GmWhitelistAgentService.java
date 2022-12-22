package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmWhitelistAgentEntity;

import java.util.Map;

/**
 * 代理白名单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
public interface GmWhitelistAgentService extends IService<GmWhitelistAgentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

