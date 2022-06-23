package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmMiningInfoEntity;

import java.util.Map;

/**
 * 用户挖矿属性表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-12 19:36:10
 */
public interface GmMiningInfoService extends IService<GmMiningInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

