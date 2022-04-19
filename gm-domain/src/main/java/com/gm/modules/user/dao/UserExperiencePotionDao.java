package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserExperiencePotionEntity;
import com.gm.modules.user.rsp.UserExpInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 玩家经验药水信息表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
@Mapper
public interface UserExperiencePotionDao extends BaseMapper<UserExperiencePotionEntity> {
    List<UserExpInfoRsp> getUserEx(UserExperiencePotionEntity userExperiencePotionEntity);
    List<UserExperiencePotionEntity> getUserNotUseEx(UserExperiencePotionEntity userExperiencePotionEntity);
}
