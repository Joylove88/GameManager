package com.gm.modules.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
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
     * @param map
     * @return
     */
    List<UserHeroFragInfoRsp> getUserAllHeroFrag(Map<String, Object> map);

    /**
     * 获取升星时玩家英雄的碎片数量
     * @param map
     * @return
     */
    UserHeroFragInfoRsp getUserAllHeroFragCount(Map<String, Object> map);

    /**
     * 消耗英雄碎片升星
     * @param map
     */
    void depleteHeroFrag(Map<String, Object> map);

    /**
     * 查询我的英雄碎片
     * @param userId
     * @param params
     * @return
     */
    PageUtils queryUserHeroFrag(Long userId, Map<String, Object> params);

    /**
     * 根据用户ID 和 英雄碎片ID 查询我的这个英雄碎片
     * @param userId
     * @param userHeroFragId
     * @return
     */
    UserHeroFragEntity getUserHeroById(Long userId ,Long userHeroFragId);
}

