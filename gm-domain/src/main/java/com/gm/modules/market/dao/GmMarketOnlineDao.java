package com.gm.modules.market.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.market.entity.GmMarketOnlineEntity;
import com.gm.modules.user.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 市场在售物品
 *
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:38
 */
@Mapper
public interface GmMarketOnlineDao extends BaseMapper<GmMarketOnlineEntity> {

    List<UserHeroEntity> queryUserOnMarketHero(@Param("userId") Long userId);

    List<UserHeroFragEntity> queryUserOnMarketHeroFrag(@Param("userId") Long userId);

    List<UserEquipmentEntity> queryUserOnMarketEquipment(@Param("userId") Long userId);

    List<UserEquipmentFragEntity> queryUserOnMarketEquipmentFrag(@Param("userId") Long userId);

    List<UserExperiencePotionEntity> queryUserOnMarketExperiencePotion(@Param("userId") Long userId);
}
