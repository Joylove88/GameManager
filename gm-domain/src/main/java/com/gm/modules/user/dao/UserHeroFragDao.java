package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.rsp.UserHeroFragInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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
     * @param map
     * @return
     */
	List<UserHeroFragInfoRsp> getUserAllHeroFrag(Map<String, Object> map);

    /**
     * 获取升星时玩家英雄的碎片数量
     * @param map
     * @return
     */
    UserHeroFragInfoRsp getUserAllHeroFragCount(Map<String, Object> map);

    /**
     * 消耗英雄碎片升星
     * @param map
     */
	void depleteHeroFrag(Map<String, Object> map);
}
