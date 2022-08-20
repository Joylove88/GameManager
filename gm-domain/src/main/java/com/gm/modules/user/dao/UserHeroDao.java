package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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
	 * @param map
	 * @return
	 */
	List<UserHeroInfoRsp> getUserAllHero(Map<String, Object> map);

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
}
