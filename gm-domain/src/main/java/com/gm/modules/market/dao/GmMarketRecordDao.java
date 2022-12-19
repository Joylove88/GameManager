package com.gm.modules.market.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.market.entity.GmMarketRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 市场交易订单
 * 
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:36
 */
@Mapper
public interface GmMarketRecordDao extends BaseMapper<GmMarketRecordEntity> {
	
}
