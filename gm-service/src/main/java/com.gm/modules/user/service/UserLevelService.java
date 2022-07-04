package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserLevelEntity;

import java.util.Map;

/**
 * 玩家等级表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-02 15:28:42
 */
public interface UserLevelService extends IService<UserLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

