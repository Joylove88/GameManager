package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserExperiencePotionEntity;
import com.gm.modules.user.req.UseExpReq;
import com.gm.modules.user.rsp.UserExpInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 玩家经验药水信息表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
public interface UserExperiencePotionService extends IService<UserExperiencePotionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<UserExpInfoRsp> getUserEx(UserExperiencePotionEntity userExperiencePotionEntity);

    boolean userHeroUseEx(UserEntity user, UseExpReq useExpReq);

    List<UserExperiencePotionEntity> queryUserExperiencePotion(UserEntity user);
}

