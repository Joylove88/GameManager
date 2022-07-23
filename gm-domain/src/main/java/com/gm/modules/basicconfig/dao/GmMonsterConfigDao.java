package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.GmMonsterConfigEntity;
import com.gm.modules.basicconfig.rsp.MonsterInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 怪物配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Mapper
public interface GmMonsterConfigDao extends BaseMapper<GmMonsterConfigEntity> {
    /**
     * 获取怪物信息
     * @param monsterConfigEntity
     * @return
     */
    List<MonsterInfoRsp> getMonsterInfo(GmMonsterConfigEntity monsterConfigEntity);
	
}
