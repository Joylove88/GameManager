package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:10:34
 */
@Mapper
public interface UserHeroDao extends BaseMapper<UserHeroEntity> {
	List<UserHeroEntity> getUserHeroHash(UserHeroEntity userHeroEntity);

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
	UserHeroInfoRsp getUserHeroById(@Param("userHeroId")Long userHeroId);
}
