package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.rsp.UserHeroFragInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-06 18:25:15
 */
@Mapper
public interface UserHeroFragDao extends BaseMapper<UserHeroFragEntity> {

    /**
     * 获取玩家英雄碎片
     * @param userId
     * @return
     */
	List<UserHeroFragInfoRsp> getUserAllHeroFrag(Long userId);
}
