package com.gm.modules.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.market.entity.GmMarketRecordEntity;

import java.util.Map;

/**
 * 市场交易订单
 *
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:36
 */
public interface GmMarketRecordService extends IService<GmMarketRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

