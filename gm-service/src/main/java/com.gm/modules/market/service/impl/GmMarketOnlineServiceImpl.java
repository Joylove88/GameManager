package com.gm.modules.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.market.dao.GmMarketOnlineDao;
import com.gm.modules.market.dto.PutOnMarketReq;
import com.gm.modules.market.entity.GmMarketOnlineEntity;
import com.gm.modules.market.service.GmMarketOnlineService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("gmMarketOnlineService")
@Transactional
public class GmMarketOnlineServiceImpl extends ServiceImpl<GmMarketOnlineDao, GmMarketOnlineEntity> implements GmMarketOnlineService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmMarketOnlineEntity> page = this.page(
                new Query<GmMarketOnlineEntity>().getPage(params),
                new QueryWrapper<GmMarketOnlineEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void putOnMarket(UserEntity user, PutOnMarketReq putOnMarketReq) {
        // 1.插入市场在售表
        GmMarketOnlineEntity gmMarketOnlineEntity = new GmMarketOnlineEntity();
        gmMarketOnlineEntity.setGoodsType(putOnMarketReq.getItemsType());
        gmMarketOnlineEntity.setGoodsId(putOnMarketReq.getItemsId());
        gmMarketOnlineEntity.setUserId(user.getUserId());
        gmMarketOnlineEntity.setSellPrice(putOnMarketReq.getSellPrice());
        gmMarketOnlineEntity.setStatus(0);
        gmMarketOnlineEntity.setCreateTime(new Date());
        gmMarketOnlineEntity.setCreateTimeTs(gmMarketOnlineEntity.getCreateTime().getTime());
        baseMapper.insert(gmMarketOnlineEntity);
    }

    @Override
    public List<UserHeroEntity> queryUserOnMarketHero(Long userId) {

        return null;
    }

    @Override
    public PageUtils queryUserOnMarketHeroFrag(Long userId) {
        return null;
    }

}
