package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserExperienceEntity;
import com.gm.modules.user.req.UseExpPropReq;
import com.gm.modules.user.rsp.UserExpInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 玩家经验道具信息
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
public interface UserExperienceService extends IService<UserExperienceEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取玩家经验道具
     * @param map
     * @return
     */
    List<UserExpInfoRsp> getUserExp(Map<String, Object> map);

    /**
     * 获取玩家未使用的经验道具
     * @param map
     * @return
     */
    List<UserExperienceEntity> getUnusedExpFromPlayers(Map<String, Object> map);

    /**
     * 使用经验道具
     * @param user
     * @param useExpPropReq
     * @return
     */
    boolean userHeroUseExp(UserEntity user, UseExpPropReq useExpPropReq);

    /**
     * 查询我的物品 分页
     * @param userId
     * @param params
     * @return
     */
    PageUtils queryUserExperience(Long userId, Map<String, Object> params);

    /**
     * 查询用户的经验道具
     * @param id
     * @return
     */
    UserExperienceEntity getUserExperienceById(Long userId , Long id);
}

