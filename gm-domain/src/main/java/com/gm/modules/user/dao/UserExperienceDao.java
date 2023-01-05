package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserExperienceEntity;
import com.gm.modules.user.rsp.UserExpInfoDetailRsp;
import com.gm.modules.user.rsp.UserExpInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 玩家经验道具信息
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
@Mapper
public interface UserExperienceDao extends BaseMapper<UserExperienceEntity> {
    /**
     * 获取玩家经验道具
     * @param map
     * @return
     */
    List<UserExpInfoRsp> getUserExp(Map<String, Object> map);

    /**
     * 获取玩家经验道具全部参数
     * @param map
     * @return
     */
    List<UserExpInfoDetailRsp> getUserExpDetail(Map<String, Object> map);

    /**
     * 获取玩家未使用的经验道具
     * @param map
     * @return
     */
    List<UserExperienceEntity> getUnusedExpFromPlayers(Map<String, Object> map);

    /**
     * 消耗经验道具
     */
    void useExpProp(Map<String, Object> map);
}
