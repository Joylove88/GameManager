package com.gm.modules.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.rsp.UserHeroFragInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-06 18:25:15
 */
public interface UserHeroFragService extends IService<UserHeroFragEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取玩家英雄碎片
     * @param userId
     * @return
     */
    List<UserHeroFragInfoRsp> getUserAllHeroFrag(Long userId);

}

