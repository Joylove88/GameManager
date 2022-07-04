package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
import org.apache.ibatis.annotations.Param;

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
     * @param userId
     * @return
     */
    List<UserHeroInfoRsp> getUserAllHero(Long userId);
    /**
     * 获取玩家英雄指定的英雄
     * @param userHeroId
     * @return
     */
    UserHeroInfoRsp getUserHeroById(Long userHeroId);
}

