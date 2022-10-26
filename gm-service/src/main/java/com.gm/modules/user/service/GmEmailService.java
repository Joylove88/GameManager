package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmEmailEntity;

import java.util.Map;

/**
 * 玩家邮箱
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-10-20 18:37:41
 */
public interface GmEmailService extends IService<GmEmailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

