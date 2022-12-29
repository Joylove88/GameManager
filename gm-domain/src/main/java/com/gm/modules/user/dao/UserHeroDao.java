package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserHeroInfoDetailRsp;
import com.gm.modules.user.rsp.UserHeroInfoMarketRsp;
import com.gm.modules.user.rsp.UserHeroInfoNotAllRsp;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:10:34
 */
@Mapper
public interface UserHeroDao extends BaseMapper<UserHeroEntity> {
    List<UserHeroEntity> getUserHeroHash(UserHeroEntity userHeroEntity);

    /**
     * 获取玩家英雄
     *
     * @param map
     * @return
     */
    List<UserHeroInfoRsp> getUserAllHero(Map<String, Object> map);

    /**
     * 获取玩家英雄
     *
     * @param map
     * @return
     */
    List<UserHeroInfoNotAllRsp> getUserAllHeroSimple(Map<String, Object> map);

    /**
     * 获取玩家英雄指定的英雄
     *
     * @param map
     * @return
     */
    UserHeroEntity getUserHeroById(Map<String, Object> map);

    /**
     * 获取玩家英雄指定的英雄信息
     *
     * @param map
     * @return
     */
    UserHeroInfoRsp getUserHeroByIdRsp(Map<String, Object> map);

    /**
     * 获取玩家英雄指定的英雄详细信息
     *
     * @param map
     * @return
     */
    UserHeroInfoDetailRsp getUserHeroByIdDetailRsp(Map<String, Object> map);

    /**
     * 市场：我的物品列表
     *
     * @param page
     * @param eq
     * @return
     */
    IPage<UserHeroInfoMarketRsp> marketPage(IPage<UserHeroInfoMarketRsp> page, @Param(Constants.WRAPPER) QueryWrapper<UserHeroInfoMarketRsp> eq);
}
