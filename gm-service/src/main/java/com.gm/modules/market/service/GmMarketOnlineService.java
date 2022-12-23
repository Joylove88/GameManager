package com.gm.modules.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.market.dto.PutOnMarketReq;
import com.gm.modules.market.entity.GmMarketOnlineEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.entity.UserHeroFragEntity;

import java.util.List;
import java.util.Map;

/**
 * 市场在售物品
 *
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:38
 */
public interface GmMarketOnlineService extends IService<GmMarketOnlineEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 上架物品
     * @param user
     * @param putOnMarketReq
     */
    void putOnMarket(UserEntity user, PutOnMarketReq putOnMarketReq);

    /**
     * 查询用户的在售英雄
     * @param userId
     * @return
     */
    List<UserHeroEntity> queryUserOnMarketHero(Long userId);

    /**
     * 查询用户的在售英雄碎片
     * @param userId
     * @return
     */
    List<UserHeroFragEntity> queryUserOnMarketHeroFrag(Long userId);
}

