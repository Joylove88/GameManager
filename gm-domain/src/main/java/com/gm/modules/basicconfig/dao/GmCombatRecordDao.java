package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.GmCombatRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 战斗记录表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-24 15:46:33
 */
@Mapper
public interface GmCombatRecordDao extends BaseMapper<GmCombatRecordEntity> {

    /**
     * 获取某个队伍当前时间战斗中的记录
     * @param params
     * @return
     */
    List<GmCombatRecordEntity> getCombatRecordNow(Map<String, Object> params);
}
