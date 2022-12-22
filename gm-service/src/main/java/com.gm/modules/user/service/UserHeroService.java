package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserHeroInfoNotAllRsp;
import com.gm.modules.user.rsp.UserHeroInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:10:34
 */
public interface UserHeroService extends IService<UserHeroEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取玩家英雄
     * @param map
     * @return
     */
    List<UserHeroInfoRsp> getUserAllHero(Map<String, Object> map);

    /**
     * 获取玩家英雄
     * @param map
     * @return
     */
    List<UserHeroInfoNotAllRsp> getUserAllHeroSimple(Map<String, Object> map);

    /**
     * 获取玩家英雄指定的英雄
     * @param map
     * @return
     */
    UserHeroEntity getUserHeroById(Map<String, Object> map);

    /**
     * 获取玩家英雄指定的英雄
     * @param map
     * @return
     */
    UserHeroInfoRsp getUserHeroByIdRsp(Map<String, Object> map);

    List<UserHeroEntity> queryUserHero(UserEntity user);
}

