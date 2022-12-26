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
import com.gm.modules.user.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("gmMarketOnlineService")
@Transactional
public class GmMarketOnlineServiceImpl extends ServiceImpl<GmMarketOnlineDao, GmMarketOnlineEntity> implements GmMarketOnlineService {

    @Autowired
    private GmMarketOnlineDao gmMarketOnlineDao;

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
        return gmMarketOnlineDao.queryUserOnMarketHero(userId);
    }

    @Override
    public List<UserHeroFragEntity> queryUserOnMarketHeroFrag(Long userId) {
        return gmMarketOnlineDao.queryUserOnMarketHeroFrag(userId);
    }

    @Override
    public List<UserEquipmentEntity> queryUserOnMarketEquipment(Long userId) {
        return gmMarketOnlineDao.queryUserOnMarketEquipment(userId);
    }

    @Override
    public List<UserEquipmentFragEntity> queryUserOnMarketEquipmentFrag(Long userId) {
        return gmMarketOnlineDao.queryUserOnMarketEquipmentFrag(userId);
    }

    @Override
    public List<UserExperiencePotionEntity> queryUserOnMarketExperiencePotion(Long userId) {
        return gmMarketOnlineDao.queryUserOnMarketExperiencePotion(userId);
    }

    @Override
    public PageUtils queryUserHero(Map<String, Object> params) {
        IPage<UserHeroEntity> page = gmMarketOnlineDao.pageHeroMarket(
                new Query<UserHeroEntity>().getPage(params),
                new QueryWrapper<UserHeroEntity>()
                        .eq("a.GOODS_TYPE", 0)
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUserHeroFrag(Map<String, Object> params) {
        IPage<UserHeroFragEntity> page = gmMarketOnlineDao.pageHeroFragMarket(
                new Query<UserHeroFragEntity>().getPage(params),
                new QueryWrapper<UserHeroFragEntity>()
                        .eq("a.GOODS_TYPE", 0)
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUserEquipment(Map<String, Object> params) {
        IPage<UserEquipmentEntity> page = gmMarketOnlineDao.pageEquipmentMarket(
                new Query<UserEquipmentEntity>().getPage(params),
                new QueryWrapper<UserEquipmentEntity>()
                        .eq("a.GOODS_TYPE", 0)
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUserEquipmentFrag(Map<String, Object> params) {
        IPage<UserEquipmentFragEntity> page = gmMarketOnlineDao.pageEquipmentFragMarket(
                new Query<UserEquipmentFragEntity>().getPage(params),
                new QueryWrapper<UserEquipmentFragEntity>()
                        .eq("a.GOODS_TYPE", 0)
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUserExperiencePotion(Map<String, Object> params) {
        IPage<UserExperiencePotionEntity> page = gmMarketOnlineDao.pageExperiencePotionMarket(
                new Query<UserExperiencePotionEntity>().getPage(params),
                new QueryWrapper<UserExperiencePotionEntity>()
                        .eq("a.GOODS_TYPE", 0)
        );
        return new PageUtils(page);
    }

}
