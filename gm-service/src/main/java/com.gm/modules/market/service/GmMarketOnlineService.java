package com.gm.modules.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.market.dto.PutOnMarketReq;
import com.gm.modules.market.entity.GmMarketOnlineEntity;
import com.gm.modules.user.entity.UserEntity;

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
}

