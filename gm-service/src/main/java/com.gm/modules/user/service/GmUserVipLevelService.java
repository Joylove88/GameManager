package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmUserVipLevelEntity;

import java.util.Map;

/**
 * 用户消费等级
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-23 15:25:58
 */
public interface GmUserVipLevelService extends IService<GmUserVipLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

